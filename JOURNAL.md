# JOURNAL — Challenge B: bootstrap

Goal of this file: get the repo ready to start implementing the gateway. It does not yet cover the action flow, the confirmation UI, or the HTTPS tunnel.

Starting decisions:

- Domain: simulated bank transfer (fits Bank Assistant; no real-world effects).
- Backend: MCP server in Java (Spring Boot + Spring AI MCP, Streamable HTTP transport).
- Frontend: React + Vite + TypeScript.
- The MCP must end up on a public HTTPS URL; that is why the scaffold uses HTTP, not stdio.

---

## 1. Check tools

You need:

- JDK 17+ (21 LTS is fine; 17 is enough for Spring Boot 3/4 + Spring AI MCP)
- Maven Wrapper from Spring Initializr (`./mvnw`). A global `mvn` is optional
- Node.js 20+ and npm
- Git
- MCP Inspector (`npx @modelcontextprotocol/inspector`) to test the MCP locally

Check:

```bash
java -version
mvn -version
node -v
npm -v
git --version
```

## 2. Create the repo structure

In this directory:

```text
.
├── backend/     # MCP server + HTTP API for review/confirmation
├── frontend/    # React + Vite
├── .gitignore
├── .env.example
└── JOURNAL.md
```

Initialize Git if it does not exist yet. `.gitignore` must exclude `.env`, `target/`, `node_modules/`, and `dist/`.

## 3. Scaffold the Java backend

Generate a Spring Boot app in `backend/` with:

-   
- MCP dependency: `spring-ai-starter-mcp-server-webmvc`
- tests: Spring Boot Test

Minimum configuration to write down (`application.yml` or equivalent):

- `server.port` (for example `8080`)
- `spring.ai.mcp.server.protocol=STREAMABLE`
- MCP endpoint at `/mcp`

Start it and check that it responds at `http://localhost:8080`.

## 4. Scaffold the React + Vite frontend

```bash
npm create vite@latest frontend -- --template react-ts
cd frontend
npm install
npm run dev
```

Check that the dev server starts (usually `http://localhost:5173`).

No action UI is needed yet: it is enough that the project compiles.

## 5. Secrets and environment variables

Create `.env.example` (no real values) with at least:

- `MCP_AUTH_TOKEN` — token that AIFindr will send to the MCP (do not hardcode)
- `APP_BASE_URL` — public HTTPS URL once a tunnel exists
- `CORS_ALLOWED_ORIGIN` — frontend origin

Create a local `.env` from that example. Do not commit secrets or AIFindr API keys.

## 6. Verify you can start implementing

Bootstrap checklist (all local):

1. `backend` starts and the `/mcp` endpoint exists.
2. MCP Inspector connects to `http://localhost:8080/mcp`.
3. `frontend` starts with Vite.
4. Git ignores secrets and build artifacts.

Once those four points pass, the next work is no longer scaffolding: define the action state model, the MCP tools, and the review/confirmation UI.

## Out of this bootstrap (do not block day 1)

- ngrok / Cloudflare Tunnel
- Registering the MCP in AIFindr DEV
- Persistence, audit, expiration, adversarial tests
