# Frontend compatibility smoke test

Run `npm ci --ignore-scripts`, `npx playwright install chromium`,
`VITE_API_URL=/api npm run build`, then `npm run test:browser`.
Use `CHROME_PATH` to select an installed Chromium executable instead.
The explicit build-time API URL overrides the checked-in deployment URL so
all API requests remain on the local preview server.

The test opens the actual production bundle in Chromium. It checks public
pages, unauthenticated route protection, login JSON, bearer headers,
authenticated routes, auth validation after reload, and clearing auth state
on a 401 response. API responses are synthetic browser-intercepted fixtures.
All non-local requests are blocked. No real accounts, backend, database,
model generation, or publishing are used or verified.

The dependency update removes the production audit findings present before
this change. This is a compatibility check, not a red/green test for those
security advisories. Development-tool advisories and the pre-existing
missing ESLint configuration are not fixed or claimed clean here.
