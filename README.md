# SathwikFlow

Focused project-management app inspired by JIRA, organized as a simple modular monolith:

- `backend/` - Spring Boot API, JPA entities, Flyway migrations, security and feature packages.
- `frontend/` - React + TypeScript + Vite SPA.
- `scripts/` - local helper scripts.
- `tmp/` - local scratch space, ignored by Git.
- `artifacts/` - screenshots and generated portfolio artifacts.

## Run Locally

```bash
docker compose up -d mysql mailpit
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

The first implementation keeps the structure from the provided project tree and lays down the foundation for the MVP: auth/session tables, project roles, workflow statuses, issues, epics, sprints, comments, activity history, and notification outbox.
