# Local Testing Guide

## Requirements

- Java 17 or later for the Spring Boot backend.
- Maven, or a project-provided Maven wrapper named `mvnw` / `mvnw.cmd`.
- Node.js and npm for the React + Vite frontend.
- PostgreSQL running locally.
- A complete backend project with `pom.xml` and `src/main/resources/application.properties` or `application.yml`.
- A complete frontend project with `package.json` and `vite.config.js` or `vite.config.ts`.

Verified on this machine:

- Java is installed: OpenJDK 17.
- Node.js is installed.
- npm is installed.
- PostgreSQL port `5432` is accepting TCP connections.

Current blockers found in this workspace:

- `mvn` is not available on PATH.
- `psql` is not available on PATH.
- `pg_isready` is not available on PATH.
- `backend/pom.xml` was not found.
- `backend/src/main/resources/application.properties` was not found.
- `backend/src/main/resources/application.yml` was not found.
- `frontend/package.json` was not found.
- `frontend/vite.config.js` or `frontend/vite.config.ts` was not found.

## Starting Database

Start PostgreSQL using your local installation method, usually Windows Services or pgAdmin.

To check the port from PowerShell:

```powershell
Test-NetConnection localhost -Port 5432
```

Expected:

```text
TcpTestSucceeded : True
```

Then verify the backend configuration file:

```text
backend/src/main/resources/application.properties
```

or:

```text
backend/src/main/resources/application.yml
```

Check these values:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<database-name>
spring.datasource.username=<database-username>
spring.datasource.password=<database-password>
```

The configured database must exist in PostgreSQL before the backend starts.

## Starting Backend

Go to the backend folder:

```powershell
cd backend
```

If Maven is installed:

```powershell
mvn spring-boot:run
```

If the project includes a Maven wrapper:

```powershell
.\mvnw spring-boot:run
```

Expected:

- Backend starts without errors.
- Spring Boot listens on port `8080`, unless configured otherwise.
- Logs show successful application startup.
- Logs do not show database connection errors.

Verify port `8080`:

```powershell
Test-NetConnection localhost -Port 8080
```

If Spring Actuator is enabled, verify:

```text
http://localhost:8080/actuator/health
```

If Actuator is not enabled, verify with an existing API endpoint such as register or login.

## Starting Frontend

Go to the frontend folder:

```powershell
cd frontend
```

Install dependencies:

```powershell
npm install
```

Start Vite:

```powershell
npm run dev
```

Expected:

- Frontend starts without errors.
- Vite listens on port `5173`.
- Browser should open:

```text
http://localhost:5173
```

Do not open the React page from:

```text
http://localhost:8080
```

Port `8080` is for backend APIs. Port `5173` is for the React development app.

## User Registration Flow

1. Open the frontend:

```text
http://localhost:5173
```

2. Go to the register page.
3. Create a new student account.
4. Confirm the backend returns a successful response.
5. Confirm the app redirects or shows the expected success state.

## Login Flow

1. Open the login page in the React app.
2. Enter the registered email and password.
3. React sends the login request to the Spring Boot backend.
4. Spring Boot verifies the user.
5. Spring Boot returns a JWT token.
6. React stores the token, usually in local storage.
7. Protected pages should now be accessible.

To verify the token in the browser:

1. Open DevTools.
2. Go to Application.
3. Check Local Storage.
4. Look for the JWT token key used by the app.

## Student Profile Testing

After login:

1. Open the student dashboard.
2. Open the Student Profile page.
3. Fill in profile details.
4. Save the profile.
5. Refresh the page.
6. Confirm the profile data loads again.
7. Update one or more fields.
8. Save again.
9. Confirm the updated data remains after another refresh.

Expected backend API behavior:

```http
POST /student/profile
```

Creates the logged-in student's profile.

```http
GET /student/profile
```

Returns the logged-in student's saved profile.

```http
PUT /student/profile
```

Updates the logged-in student's existing profile.

All protected profile requests must include:

```http
Authorization: Bearer <jwt-token>
```

## Common Errors

### ERR_CONNECTION_REFUSED on localhost:8080

Root cause:

The backend is not running, or it failed during startup.

Layer:

Backend.

Check:

```powershell
Test-NetConnection localhost -Port 8080
```

### ERR_CONNECTION_REFUSED on localhost:5173

Root cause:

The frontend Vite dev server is not running.

Layer:

Frontend.

Check:

```powershell
Test-NetConnection localhost -Port 5173
```

### mvn is not recognized

Root cause:

Maven is not installed or not added to PATH.

Layer:

Local development environment.

Fix:

Install Maven or use the project's Maven wrapper if available.

### Database connection refused

Root cause:

PostgreSQL is not running, the port is wrong, or the backend database config points to the wrong host.

Layer:

Database/backend configuration.

Check:

```powershell
Test-NetConnection localhost -Port 5432
```

### JWT missing or invalid

Root cause:

User is not logged in, token was not stored, token expired, or frontend is not sending the `Authorization` header.

Layer:

Authentication/frontend API service.

Check browser local storage and request headers in DevTools Network tab.
