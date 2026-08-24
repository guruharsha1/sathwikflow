#!/usr/bin/env bash
set -euo pipefail
docker compose up -d mysql mailpit
echo "Start the API with: cd backend && mvn spring-boot:run"
echo "Start the SPA with: cd frontend && npm install && npm run dev"

