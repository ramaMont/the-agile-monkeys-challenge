# Secure Actions Gateway

Design document for **Part 2 (Challenge B)** of the AIFindr technical assessment: a safe-actions gateway exposed as an MCP server.

The solution lets the Bank Assistant agent **register** a simulated appointment request and requires **human confirmation** in a web UI before the action is considered settled. No transfers, bookings, or other effects are applied to real systems.


| Environment              | URL                                                                                        |
| ------------------------ | ------------------------------------------------------------------------------------------ |
| Gateway (MCP + API)      | [https://secure-actions-gateway.onrender.com](https://secure-actions-gateway.onrender.com) |
| Review UI                | [https://gateway-ui.onrender.com](https://gateway-ui.onrender.com)                         |
| MCP endpoint for AIFindr | `https://secure-actions-gateway.onrender.com/mcp`                                          |


Local setup, environment variables, and deployment: [backend/README.md](backend/README.md) and [frontend/README.md](frontend/README.md).

---

## Priority

The primary goal was a **minimum viable product**: a thin, deployed slice that is usable end to end, delivers the confirmation loop the brief asks for, and can be iterated. It is an MVP that answers “can an operator actually review a proposed action in a live environment, and is the pattern worth extending?”. The implementation is therefore kept small on purpose, with the expectation that later work (expiry, idempotency, audit, an identity provider) builds on this same product rather than replacing it.

Security was treated as a property of **who may speak to whom**, not as a full control catalogue:

- **MCP.** `/mcp` is an OAuth 2.1 resource server. A request without a Bearer token is rejected (`401` and a challenge). Tokens are issued by this gateway’s Authorization Server through authorization code + PKCE, with a registered client and a redirect allowlist limited to AIFindr (`*.aifindr.ai`) and loopback. The intended caller is the Custom MCP connection in AIFindr DEV, not an anonymous client.
- **Review API.** CORS allows a single browser origin (the SPA). Every `/v1` route except login (and `OPTIONS`) requires a Bearer JWT. The SPA obtains that token only after a successful `POST /v1/auth/login`. Health and OAuth discovery remain public so the service can be probed and so AIFindr can start the authorization flow.

The result is a working product in production, with a communication boundary around the MCP and around the review API, designed to be validated and then improved.

---

## Architecture

The agent has no execution tool. Its only write to the system is creating a proposal in `PENDING` status. The “simulated execution” required by the brief is the state transition performed by an authenticated reviewer (`APPROVED` or `REJECTED`).

```text
End user  →  AIFindr (Bank Assistant)
                 │  OAuth 2.1 + PKCE
                 ▼
          MCP  POST /mcp
          propose_reservation
          get_reservation_proposal_status
                 │
                 ▼
          Gateway (Spring Boot)  ──  PostgreSQL
                 │
                 │  JWT (SPA)
                 ▼
          React UI  →  sign-in, list, APPROVED | REJECTED
```

In production (Render) the solution is split across three resources:


| Resource      | Service type         | Responsibility                                             |
| ------------- | -------------------- | ---------------------------------------------------------- |
| `backend/`    | Web Service (Docker) | MCP server, OAuth Authorization Server, and REST API `/v1` |
| PostgreSQL 16 | Database             | Persistence of proposals and the reviewer user             |
| `frontend/`   | Static Site          | Human review interface                                     |


Main flow:

1. The user asks the agent for a branch appointment.
2. The agent calls `propose_reservation`. The gateway persists a `PENDING` proposal and returns an `id`.
3. The agent communicates that `id` to the user.
4. An operator signs in to the UI, reviews the list, and approves or rejects the proposal.
5. Status can be read from the UI or, through the agent, via `get_reservation_proposal_status`.

The state machine is `PENDING → APPROVED | REJECTED`. Transitions are terminal. A `PATCH` on a proposal that is no longer `PENDING` returns `409 Conflict`.

---

## Main decisions

**Action domain.** An appointment is modelled instead of a transfer. The pattern required by the challenge (propose, confirm, query status) is still met, and the domain stays aligned with Bank Assistant, without simulating movement of funds.

**Separation of proposal and confirmation.** The `propose_reservation` tool only inserts a `PENDING` record. There is no MCP tool that approves. Explicit confirmation happens in the web UI, under a session distinct from the agent’s. The language model therefore cannot close the authorization loop by itself.

**Platform.** The backend is a single Spring Boot process (Java 17) with Spring AI MCP. It hosts Streamable HTTP transport (`POST /mcp`), the Authorization Server that AIFindr DEV expects when connecting a Custom MCP, and a JWT resource server for the SPA. Authentication of the MCP channel is an integration requirement, not an optional extra.

**Distinct identities.** `MCP_OAUTH_USER` / `MCP_OAUTH_PASSWORD` authenticate the HTML form of the authorization-code flow (AIFindr connection). `ADMIN_OAUTH_USER` / `ADMIN_OAUTH_PASSWORD` seed the SPA user. The agent proposes; the reviewer decides.

**OAuth 2.1 rather than a static API key.** AIFindr uses authorization code, PKCE, and a client secret. Redirects are limited to `*.aifindr.ai` hosts (HTTPS) and loopback. There is no Dynamic Client Registration or CIMD. The RSA signing key is stored as a secret; if it is regenerated on every deploy, both SPA JWTs and tokens issued to AIFindr become invalid.

**Persistence.** Proposals are stored in PostgreSQL so they survive Web Service restarts. The schema is updated with `ddl-auto: update`, which is adequate for the scope of the assessment.

**Review UI.** The SPA is limited to authentication, a paginated list, the approve/reject decision, and periodic refresh. The goal is the confirmation control, not a full operations console.

**Hosting.** The brief allows a temporary tunnel. A Render Web Service and Static Site were chosen so the MCP HTTPS URL stays stable and the server does not need to be re-registered in AIFindr every time a tunnel drops.

---

## Assumptions

- There is a single default reviewer and a single project in the DEV environment. Multi-tenancy and authorization by customer or branch are not modelled.
- “Execute in a simulated way” is interpreted as persisting and displaying `APPROVED` or `REJECTED`. There is no integration with a banking core or an appointment system.
- `allowedTools` and the prompt in AIFindr restrict which tools the agent can see. The gateway does not treat that restriction as the only security control, which is why it does not expose an execution tool.
- The service `OAUTH_CLIENT_ID` and `OAUTH_CLIENT_SECRET` match those configured on the Custom MCP in AIFindr.
- CORS allows a single origin (the UI on Render, or `http://localhost:5173` locally).
- The person who approves is a service operator, not the chat user.

---

## Trade-offs


| Decision                                           | Benefit                                         | Limitation                                                                            |
| -------------------------------------------------- | ----------------------------------------------- | ------------------------------------------------------------------------------------- |
| Confirm in the UI, not in the chat thread          | The agent cannot self-approve the action        | The end user does not sign the operation in the conversation; an operator is involved |
| Approve = persisted state transition               | No real-world effects; a safe demonstration     | No external system or booking mock is invoked                                         |
| One process for MCP, API, and Authorization Server | Fewer services; the same OAuth issuer           | Heavier to operate than a minimal MCP on another runtime                              |
| Polling in the UI                                  | Fits a static site, with no push infrastructure | New changes are not visible immediately                                               |
| OAuth client registered in memory                  | Explicit configuration, no dynamic registration | Rotating the secret requires a redeploy                                               |
| An administrator user seeded at startup            | Enables the happy path without an IdP           | No SSO, roles, or reviewer management                                                 |
| Paginated list without server-side filters         | Covers review of the minimum viable set         | No search and no `PENDING`-only queue in the API                                      |


---

## Risks

- **Excess proposals from the agent** (conflicting instructions, hallucination, or an insistent user). Mitigation: there is no execution tool; impact is limited to `PENDING` rows. Residual: noise in the review queue.
- **Incorrect OAuth configuration** (issuer not equal to the public URL, missing PEM, client secret different from AIFindr’s). The panel does not complete the connection. Mitigation: `/.well-known` metadata, a stable PEM in the Render environment, and token-endpoint tests with `DelegatingPasswordEncoder`.
- **Cold start on Render’s free plan.** The first request to the MCP may fail until the service is running again. This is treated as acceptable in the context of the assessment.
- **SPA authorization.** A valid JWT can approve or reject any proposal. That is consistent with a single reviewer; it would not be in a multi-branch deployment.
- **No idempotency on** `propose_reservation`**.** A repeated request creates distinct rows. The reviewer may see duplicates; there is no double charge because there is no charge.
- **No expiry on** `PENDING`**.** An old proposal remains approvable.
- **Example credentials** `user` **/** `password`**.** Valid for local development. They must be replaced in the deployed environment.

---

## Out of scope

The deliberate scope is an **end-to-end happy path** plus a control the language model cannot bypass. OAuth integration with AIFindr and the state machine were prioritized over a wide feature list.

This delivery does not include:

- Transfers, payments, account data, or other effects on real systems.
- Idempotency keys, confirmation expiry, and queues with an SLA.
- Append-only audit, evidence signing, and export.
- Isolation between customers, branches, or reviewers.
- Retries and reconciliation with an external system (no such system exists).
- Rate limiting, WAF, captcha, or second-factor authentication.
- E2E tests of the frontend and of the AIFindr conversation. The demonstration of the agent flow is presented separately.

---

## How the behaviour is verified

The measurement for this delivery is the backend suite plus the deployed happy path. From the repo root:

```bash
cd backend && ./mvnw test
```

| Case | What it shows |
| ---- | ------------- |
| Approve or reject a proposal that is no longer `PENDING` | Illegal transition → `409` |
| Blank field, `dateTime` `not-a-date`, or an oversized string on propose | Deterministic rejection → `400` |
| `POST /mcp` without a Bearer token | `401` and a `WWW-Authenticate` challenge |
| Token endpoint with a wrong client secret | `401`; a valid `{noop}` secret with a bad code → `400` |
| Redirect URI on a foreign host or `http` to aifindr | Allowlist rejection |
| SPA login with invalid credentials | `401` |

Live URLs (happy path, HTTPS):

- Gateway (MCP + API): [https://secure-actions-gateway.onrender.com](https://secure-actions-gateway.onrender.com)
- Review UI: [https://gateway-ui.onrender.com](https://gateway-ui.onrender.com)
- MCP endpoint: `https://secure-actions-gateway.onrender.com/mcp`

There is no latency or cost benchmark. The objective evidence is the state machine, the auth boundaries, and that the public HTTPS integration stays reachable.

---

## Proposed next steps

A reasonable next increment, if the scope were extended, would be expiry of `PENDING`, an idempotency key on the proposal tool, and an audit log of who approved or rejected each action.

Schema management should move from Hibernate `ddl-auto: update` to **Flyway** migrations. Automatic updates are enough for an MVP on a single environment; they do not give a reviewable history of schema changes, nor a safe path across deploys. Versioned migrations would make the database evolve explicitly with the code.

It would also be appropriate to delegate SPA identity to an OAuth provider such as Clerk or Auth0. That would remove token issuance, rotation, and session handling from this backend, which currently signs JWTs with a local RSA key and seeds a local user. That approach was kept for simplicity: one process, no extra vendor, and a self-contained demo. AIFindr’s Custom MCP would still use this gateway as its Authorization Server; the provider would apply to reviewer login in the UI, not to the agent connection.
