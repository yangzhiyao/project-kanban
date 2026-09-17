# AGENTS.md

## Project

项目看板 - Information management system for comprehensive project overview.

## Tech Stack

- **Frontend:** Ant Design of Angular v22
- **Backend:** Spring Boot 4.0.8 GA
- **Persistence:** JPA
- **Database:** H2 (local embedded)

## Environments

| Environment | DB Behavior |
|-------------|-------------|
| Development | Resets & reinitializes on every start |
| Test | Preserves data across restarts |
| Production | Preserves data across restarts |

## Project Structure

```
backend/                    # Spring Boot application
  src/main/java/com/kanban/project/
    controller/             # REST controllers
    service/                # Business logic
    repository/             # JPA repositories
    model/                  # Entity classes
    dto/                    # Data transfer objects
    config/                 # Configuration classes
  src/main/resources/
    application.yml         # Base config
    application-dev.yml     # Dev: H2 in-memory, create-drop
    application-test.yml    # Test: H2 in-memory, update
    application-prod.yml    # Prod: H2 file-based, validate

frontend/                   # Angular application
  src/app/
    components/             # Reusable UI components
    pages/                  # Page components
    services/               # API services
    models/                 # TypeScript interfaces
    guards/                 # Route guards
```

## Commands

### Quick start (Windows, double-click)
- `start-all.cmd` — starts backend and frontend in separate windows
- `start-backend.cmd` — backend only (`http://localhost:8080`)
- `start-frontend.cmd` — frontend only (`http://localhost:4200`)

### Backend
- Build: `cd backend && ./mvnw clean package`
- Run: `cd backend && ./mvnw spring-boot:run`
- Run with profile: `cd backend && ./mvnw spring-boot:run -Dspring.profiles.active=prod`
- Test: `cd backend && ./mvnw test`

### Frontend
- Install deps: `cd frontend && npm install`
- Run dev: `cd frontend && npm start`
- Build: `cd frontend && npm run build`
- Test: `cd frontend && npm test`

## API Proxy

Frontend proxies `/api/*` to `http://localhost:8080` (see `frontend/proxy.conf.json`).
