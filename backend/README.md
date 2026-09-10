# Secure Actions MCP Gateway (backend)

Java MCP server for Challenge B: a safe-actions gateway. The Bank Assistant agent proposes a **simulated bank-branch appointment** (`turno`). Nothing here performs a real transfer or books a real slot.

## Why this challenge and this domain

I chose this challenge because adding extra capabilities to the agent is the interesting part of the problem: not only answering from the knowledge base, but proposing an action that a human can confirm.

I used branch appointments rather than transfers. In the current AIFindr product model, and in the assigned Bank Assistant project in particular, **end users are not authenticated**. The conversation does not carry a proof of who is speaking. Under that constraint a money-movement tool is unsafe. A path such as Charlie causing Alice to send funds to Bob cannot be prevented in the model as it stands: the agent has no reliable binding between the chat participant and an account holder.

That binding could exist if users signed in first and a token travelled with the conversation. The chat could then know which user is requesting the action and deny access to other people’s data or operations. Today even the operator who reviews proposals cannot tell whether Alice is the person in the chat who asked to pay Bob; they only see the proposal payload.

Appointments avoid that hole. They still exercise the full propose → review → status loop, so it is possible to learn whether the extra agent capability is useful, without simulating a transfer that the product cannot attribute to a real customer.

A proposal is created as `PENDING`. A human reviews it in the React UI and **approves** or **rejects** it. Only then is the action considered decided.

Production:

| Piece | URL |
| --- | --- |
| Gateway (this service) | [https://secure-actions-gateway.onrender.com](https://secure-actions-gateway.onrender.com) |
| Review UI | [https://gateway-ui.onrender.com](https://gateway-ui.onrender.com) |

## Stack

| Piece | Choice |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.1.1 |
| MCP | Spring AI 2.0.1 (`spring-ai-starter-mcp-server-webmvc`) |
| Transport | Streamable HTTP, sync (`POST /mcp`) |
| Auth | OAuth 2.1 (AIFindr) + JWT for the SPA (`POST /v1/auth/login`) |
| Database | PostgreSQL 16 |
| Build | Maven Wrapper (`./mvnw`) — a global `mvn` is not required |

## What it exposes

**MCP tools** (Bearer token from AIFindr OAuth):

- `propose_reservation` — create a `PENDING` proposal; returns an `id`. Rejects blank fields, strings over the length cap, and `dateTime` that is not ISO-8601 (`400`)
- `get_reservation_proposal_status` — look up a proposal by `id`

**HTTP API** (SPA JWT, except login and health):

| Method | Path | Notes |
| --- | --- | --- |
| `GET` | `/health` | Public. `{"status":"ok"}` |
| `POST` | `/v1/auth/login` | Public. Body `{ "username", "password" }` → `{ "token" }` |
| `GET` | `/v1/reservation-proposals?page=0&size=10` | Paginated list (`page >= 0`, `size` 1–50) |
| `PATCH` | `/v1/reservation-proposals/{id}` | Body `{ "status": "APPROVED" \| "REJECTED" }` |

`GET /` is not a home page (Tomcat 404 is normal).

Two logins, on purpose:

- **SPA** (`ADMIN_OAUTH_USER` / `ADMIN_OAUTH_PASSWORD`) — seeded into the `users` table
- **AIFindr OAuth form** (`MCP_OAUTH_USER` / `MCP_OAUTH_PASSWORD`) — Spring Security user for the authorization-code flow

## Local setup

Needs Docker Desktop (or Engine + Compose v2). JDK 17+ is only required for tests or a Maven build on the host.

From this directory:

```bash
cp .env.example .env
```

Do not commit `.env`.

Generate a **stable** RSA signing key (required in production; recommended locally so tokens survive restarts):

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem
```

Paste the PEM into `OAUTH_RSA_PRIVATE_KEY` as a single line with `\n` for newlines, or a quoted multiline value. If this is empty, a new key is generated on every process start and existing JWTs die.

Start Postgres and the gateway:

```bash
docker compose up --build
```

- API / MCP: [http://localhost:8080](http://localhost:8080)
- Postgres: `localhost:5432`

Stop with `Ctrl+C`, or `docker compose down`. Add `-v` to also drop the database volume.

Then start the UI from `../frontend` (see `frontend/README.md`). Sign in with `ADMIN_OAUTH_USER` / `ADMIN_OAUTH_PASSWORD` (defaults `user` / `password`).

### Environment

| Variable | Local default | Role |
| --- | --- | --- |
| `POSTGRES_*` | `gateway` | Compose database name/user/password |
| `SPRING_DATASOURCE_*` | set by Compose | JDBC URL/user/password |
| `CORS_ALLOWED_ORIGIN` | `http://localhost:5173` | SPA origin (no trailing slash) |
| `APP_BASE_URL` | `http://localhost:8080` | OAuth issuer; **must** be the public HTTPS URL on Render |
| `ADMIN_OAUTH_USER` / `ADMIN_OAUTH_PASSWORD` | `user` / `password` | SPA login |
| `MCP_OAUTH_USER` / `MCP_OAUTH_PASSWORD` | `user` / `password` | AIFindr OAuth login page |
| `OAUTH_CLIENT_ID` / `OAUTH_CLIENT_SECRET` | `aifindr` / `aifindr-dev-secret` | Must match the AIFindr MCP client |
| `OAUTH_RSA_PRIVATE_KEY` | empty (ephemeral key) | PKCS#8 PEM; keep stable |
| `OAUTH_JWK_KEY_ID` | `gateway-1` | JWK `kid` |

### Verify

```bash
curl -sS http://localhost:8080/health
# {"status":"ok"}

curl -sS -X POST http://localhost:8080/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"user","password":"password"}'
```

`POST /mcp` without a Bearer token returns **401**. That is expected.

Tests (H2 in-memory, no Docker):

```bash
./mvnw test
```

The suite covers `ReservationProposalService` (propose, status transitions, validation) and the HTTP/OAuth gates. It does **not** invoke MCP tools over JSON-RPC (`tools/call` on `POST /mcp`). That protocol-level path is out of scope: the tools are thin wrappers over the same service the tests already exercise.

## Deploying to Render

This repo is a monorepo. The backend is **not** a static site: it is a long-running Java process plus Postgres.

| Resource | Render type | How this project is set up |
| --- | --- | --- |
| `backend/` | **Web Service** (Docker) | Root directory `backend`, Dockerfile in that folder. Live at `https://secure-actions-gateway.onrender.com` |
| PostgreSQL 16 | **PostgreSQL** | Internal connection from the web service |
| `frontend/` | **Static Site** | See `frontend/README.md`. Live at `https://gateway-ui.onrender.com` |

### Web Service

1. New → Web Service → this GitHub repo.
2. Root directory: `backend`. Runtime: Docker (uses `Dockerfile`).
3. Health check path: `/health` (public; Render uses `PORT`, which `application.yml` already reads).
4. Create a Render PostgreSQL instance in the same region. In the web service, set:

   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<internal-host>:5432/<db>` (not the `postgres://` URL)
   - `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` from the database dashboard

5. Other env vars (same names as `.env.example`):

   - `APP_BASE_URL` = `https://secure-actions-gateway.onrender.com` (no trailing slash)
   - `CORS_ALLOWED_ORIGIN` = `https://gateway-ui.onrender.com`
   - `ADMIN_OAUTH_*`, `MCP_OAUTH_*`, `OAUTH_CLIENT_ID`, `OAUTH_CLIENT_SECRET`
   - `OAUTH_RSA_PRIVATE_KEY` as a **secret** (PEM; `\n` for newlines is fine)
   - `OAUTH_JWK_KEY_ID` = `gateway-1`

Without a stable PEM, every deploy mints a new signing key: SPA sessions and AIFindr tokens break.

OAuth redirect URIs are allowlisted for `https://*.aifindr.ai` and localhost. AIFindr’s callback is registered as `https://api-dev.saas.aifindr.ai/oauth/callback`.

In AIFindr, the MCP server URL is `https://secure-actions-gateway.onrender.com/mcp`, with the same `OAUTH_CLIENT_ID` / `OAUTH_CLIENT_SECRET` as Render.
