---
name: verify
description: Build/launch/drive recipe for SkillForge AI (Spring Boot backend + Vite/React frontend) on this Windows machine, where `mvn` is not on PATH and port 8080 may already be occupied by another process.
---

# SkillForge AI - verify recipe

## Gotchas on this machine

- `mvn` is NOT on PATH. A cached Maven 3.9.9 binary exists at:
  `C:\Users\vadra\.m2\wrapper\dists\apache-maven-3.9.9\977a63e90f436cd6ade95b4c0e10c20c\bin\mvn.cmd`
  Use it directly instead of failing on "mvn not found".
- Port 8080 is frequently already bound by an unrelated `java.exe` process on
  this machine (not ours to kill - don't assume it's a stale SkillForge
  instance). Run the backend on **8081** for verification via `SERVER_PORT=8081`.
- Port 5432 is often already bound by an unrelated `osra-postgres` Docker
  container (not ours - don't inspect/reuse its credentials). Spin up a
  throwaway Postgres on **5433** instead:
  `docker run -d --name skillforge-verify-pg -e POSTGRES_PASSWORD=verifypass -e POSTGRES_DB=skillforge_db -p 5433:5432 postgres:16-alpine`
- Override datasource via env vars rather than editing `application.properties`:
  `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/skillforge_db`,
  `SPRING_DATASOURCE_USERNAME=postgres`, `SPRING_DATASOURCE_PASSWORD=verifypass`.
- Launch long-running processes (`mvn spring-boot:run`, `npm run dev`) directly
  with the Bash tool's `run_in_background: true` - do NOT wrap them in your own
  `nohup ... &`, which detaches a process the harness can't track and reports
  the wrapper shell as "completed" immediately instead of the real server.
- No Playwright/browser tool is preinstalled. Chrome and Edge ARE installed.
  Install `playwright` via npm in a scratch dir and launch with
  `chromium.launch({ channel: 'msedge', headless: true })` to reuse the system
  Edge instead of downloading Playwright's bundled Chromium (much faster).
- To generate a real test PDF/DOCX for resume-upload testing without any
  extra tooling: `mvn -o dependency:build-classpath -Dmdep.outputFile=cp.txt`
  (drop `-o` the first time so it can resolve the plugin), then compile a
  throwaway `.java` file against that classpath using PDFBox
  (`PDPageContentStream` + `showText`) or POI (`XWPFDocument` +
  `createParagraph`), and run it with `java -cp ".;$(cat cp.txt)" ClassName`.

## Critical pattern: `@Transactional(readOnly = true)` on every read method that touches a LOB or LAZY field

`spring.jpa.open-in-view=false` is set, so there is no lingering Hibernate
session per request. Any service method that isn't itself `@Transactional`
and returns/touches an entity with an `@Lob` field (e.g. `Resume.content`)
or a `FetchType.LAZY` association/collection (e.g. `Resume.skills`,
`Job.requiredSkills`, `Application.job`/`.student` as `@ManyToOne`) throws
`"Unable to access lob stream"` or `LazyInitializationException` **at
serialization time**, after the repository call already returned
successfully - it looks fine until you actually hit the endpoint.

This bit every read-only service method that mapped an entity with such a
field to a DTO: `ResumeServiceImpl.getLatestForUser`, `JobServiceImpl.
listOpenJobs/listJobsForRecruiter/getJob`, `JobMatchingServiceImpl.
getRecommendedJobs`, `ApplicationServiceImpl.listForStudent/listForJob/
getById`, and `DashboardServiceImpl.getStudentStats` (via `Resume.getAtsScore`
on a detached entity carrying a Lob sibling field). All were fixed by adding
`@Transactional(readOnly = true)`. Methods that only run count/aggregate
queries or interface projections (`getRecruiterStats`, `getAdminSummary`)
are NOT affected - only ones that map a loaded entity's fields to a
response DTO are at risk.

**When adding a new read method that maps an entity to a DTO, default to
`@Transactional(readOnly = true)` unless you're sure every field it touches
is a plain, eagerly-fetched column.**

## Launch

```bash
# 1. Throwaway Postgres
docker run -d --name skillforge-verify-pg -e POSTGRES_PASSWORD=verifypass -e POSTGRES_DB=skillforge_db -p 5433:5432 postgres:16-alpine

# 2. Backend (background)
cd backend
MVN="/c/Users/vadra/.m2/wrapper/dists/apache-maven-3.9.9/977a63e90f436cd6ade95b4c0e10c20c/bin/mvn.cmd"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5433/skillforge_db"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="verifypass"
export SERVER_PORT="8081"
"$MVN" spring-boot:run > /tmp/skillforge-backend.log 2>&1   # run_in_background: true
# wait for "Started SkillforgeApplication" or "APPLICATION FAILED TO START" in the log

# 3. Frontend (background)
cd frontend
export VITE_API_BASE_URL="http://localhost:8081"
npm run dev -- --port 5173 --strictPort > /tmp/skillforge-frontend.log 2>&1   # run_in_background: true
```

## Drive

Register/login/profile via curl hits the same contract the frontend's
`authService.js` uses - good for a fast backend-only check:

```bash
curl -s http://localhost:8081/auth/register -X POST -H "Content-Type: application/json" \
  -d '{"fullName":"X","email":"x@example.com","password":"Password123","role":"STUDENT"}'
curl -s http://localhost:8081/auth/profile -H "Authorization: Bearer <token>"
```

For real UI verification, use Playwright with the system Edge (see gotchas
above) to: visit a protected route unauthenticated (expect redirect to
`/login`), register through the real form, confirm landing on the correct
role dashboard, log out/back in, reload (session should survive via the
`GET /auth/profile` call on mount), and try a role-mismatched dashboard URL
(expect bounce to `/unauthorized`).

## Cleanup

```bash
docker rm -f skillforge-verify-pg
# stop the backend/frontend background tasks (TaskStop or kill their PIDs)
```

## Known fixed issues

- `SecurityConfig` originally had no explicit `AuthenticationEntryPoint`, so
  Spring Security's default `Http403ForbiddenEntryPoint` returned **403** for
  missing/invalid JWTs instead of **401**. The frontend's axios interceptor
  (`services/api.js`) only auto-logs-out on 401, so expired sessions would
  silently fail instead of redirecting to login. Fixed by adding
  `.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))`.
