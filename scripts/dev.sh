#!/usr/bin/env bash
set -euo pipefail

docker compose up -d mysql mailpit
echo "MySQL is on 3306 and Mailpit UI is on http://localhost:8025"
