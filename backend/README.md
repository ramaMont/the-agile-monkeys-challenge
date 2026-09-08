# Secure Actions MCP Gateway (backend)

Java MCP server for Challenge B: a safe-actions gateway. The Bank Assistant agent will propose a **simulated bank transfer**. Nothing in this service performs a real transfer.

This folder is the Spring Boot scaffold only. MCP tools, confirmation flow, and the review UI are not implemented yet.

## Why this stack

AIFindr DEV must reach the MCP over a **public HTTPS URL**. The server therefore uses **Streamable HTTP** (`POST /mcp`), not stdio.

| Piece | Choice |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.1.1 |
| MCP | Spring AI 2.0.1 (`spring-ai-starter-mcp-server-webmvc`) |
| Transport | Streamable HTTP, sync |
| Database | PostgreSQL 16 (Docker Compose) |
| Build | Maven Wrapper (`./mvnw`) — a global `mvn` is not required |

## Layout

```text
backend/
├── compose.yml
├── Dockerfile
├── pom.xml
└── src/main/java/com/aifindr/gateway/
    ├── GatewayApplication.java
    ├── config/          # CORS and other cross-cutting setup
    └── reservations/   # REST + service for this resource
```

## Configuration

`src/main/resources/application.yml`:

- `server.port`: `8080`
- `spring.ai.mcp.server.protocol`: `STREAMABLE`
- `spring.ai.mcp.server.name`: `secure-actions-gateway`
- Postgres via `SPRING_DATASOURCE_*` (required; Compose sets these)
- CORS via `CORS_ALLOWED_ORIGIN` (required; no localhost default in main config)
- MCP endpoint: `http://localhost:8080/mcp`

The HTTP MCP endpoint is unauthenticated at this stage. That is acceptable on localhost. Before a public tunnel, add auth so credentials never sit in the frontend.

## Prerequisites

- Docker Desktop (or Docker Engine + Compose v2)
- JDK 17+ only for host tests / a local Maven build

```bash
cp .env.example .env
```

Do not commit `.env`.

## Run (Docker Compose)

From this directory:

```bash
docker compose up --build
```

This starts Postgres (`db`, port `5432`) and the MCP server (`backend`, port `8080`). Stop with `Ctrl+C`, or `docker compose down`. Add `-v` to `down` if you also want to drop the database volume.

Tests (H2 in-memory, no Docker):

```bash
./mvnw test
```

## Verify

`GET /` returns **404**. There is no home page yet; a 404 still means Tomcat is up.

`GET /v1/actions/reservations` returns `[]`. The React app uses this to show an empty list.

Confirm MCP with an initialize call:

```bash
curl -sS -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-03-26","capabilities":{},"clientInfo":{"name":"curl","version":"0"}}}' \
  http://localhost:8080/mcp
```

A successful response includes `"serverInfo":{"name":"secure-actions-gateway","version":"0.0.1"}`.

You can also attach [MCP Inspector](https://github.com/modelcontextprotocol/inspector) to `http://localhost:8080/mcp`. Logs may say `No tool methods found`; that is expected until tools are added.

## Notes

Spring Initializr may emit parent version `4.1.1.RELEASE`. Maven Central publishes `4.1.1`. This `pom.xml` uses `4.1.1`.

## Not in this scaffold

- Transfer tools and the action state machine
- Explicit confirmation before simulated execution
- Review API for the React UI
- Auth, HTTPS tunnel, and AIFindr DEV registration
