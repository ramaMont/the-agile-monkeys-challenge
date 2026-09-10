# Frontend

React + Vite UI for reviewing reservation proposals against the MCP gateway.

## Local setup

1. Use Node.js **22.23.2** (see `.nvmrc`). Vite needs Node 20.19+ or 22.12+.

   ```bash
   nvm use
   ```

2. Copy the env file and point the API at the local gateway (port `8080`):

   ```bash
   cp .env.example .env
   ```

   `VITE_API_BASE_URL` is required. The default `http://localhost:8080` matches a local backend.

3. Start the backend from `../backend` (Docker Compose). The SPA will not load proposals without it. See `backend/README.md`.

4. Install and run:

   ```bash
   npm install
   npm run dev
   ```

   Open [http://localhost:5173](http://localhost:5173). Sign in with the gateway admin user from `backend/.env` (`ADMIN_OAUTH_USER` / `ADMIN_OAUTH_PASSWORD`, defaults `user` / `password`).

Vite already falls back to `index.html` for unknown paths, so client-side routing works locally without extra config.

Other scripts: `npm run build`, `npm run preview`, `npm run lint`.

## Deploying to Render

Create a **static site** with:

| Setting | Value |
| --- | --- |
| Root directory | `frontend` |
| Build command | `npm ci && npm run build` |
| Publish directory | `dist` |

Set `VITE_API_BASE_URL` to the public gateway URL (build-time). On the backend, set `CORS_ALLOWED_ORIGIN` to this site’s origin.

This app uses React Router (`BrowserRouter`). On a Render static site, a typed or refreshed URL such as `/login` is requested from the CDN, which looks for a real file and returns **Not Found** unless every path is rewritten to `index.html`.

After creating the static site, open **Redirects/Rewrites** and add:

| Source | Destination | Action |
| --- | --- | --- |
| `/*` | `/index.html` | Rewrite |

See [Render: client-side routing](https://render.com/docs/deploy-create-react-app#using-client-side-routing).
