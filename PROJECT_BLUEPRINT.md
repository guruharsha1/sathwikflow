# SathwikFlow Blueprint

SathwikFlow is a compact JIRA-inspired portfolio application with:

- JWT authentication and rotating refresh sessions
- project-level roles: `PROJECT_ADMIN`, `MEMBER`, `VIEWER`
- seeded workflow columns: `BACKLOG`, `TO DO`, `IN PROGRESS`, `IN REVIEW`, `DONE`
- concurrency-safe issue numbers via `projects.next_issue_number`
- epics, sprints, comments, activity events, metrics, and email outbox

Keep the modular-monolith structure:

```text
backend/
frontend/
scripts/
tmp/
artifacts/
compose.yaml
README.md
PROJECT_BLUEPRINT.md
```

Backend packages stay feature-oriented under `com.example.jiralite`. Frontend code stays under `frontend/src` with `app`, `features`, `components`, `lib`, and `types`.
