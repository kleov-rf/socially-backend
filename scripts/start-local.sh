#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${REPO_ROOT}"

if docker compose version >/dev/null 2>&1; then
  COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE=(docker-compose)
else
  echo "Docker Compose is required but was not found." >&2
  exit 1
fi

aws_in_ministack() {
  "${COMPOSE[@]}" exec -T ministack sh -lc "$1"
}

load_db_credentials_from_secret() {
  local db_json
  db_json="$(aws_in_ministack "aws --endpoint-url http://localhost:4566 --region us-east-1 secretsmanager get-secret-value --secret-id socially/db/credentials --query SecretString --output text")"
  LOCAL_DB_HOST="$(printf '%s' "${db_json}" | python3 -c "import json,sys; print(json.load(sys.stdin)['host'])")"
  LOCAL_DB_PORT="$(printf '%s' "${db_json}" | python3 -c "import json,sys; print(json.load(sys.stdin)['port'])")"
  LOCAL_DB_NAME="$(printf '%s' "${db_json}" | python3 -c "import json,sys; print(json.load(sys.stdin)['dbname'])")"
  LOCAL_DB_USERNAME="$(printf '%s' "${db_json}" | python3 -c "import json,sys; print(json.load(sys.stdin)['username'])")"
  LOCAL_DB_PASSWORD="$(printf '%s' "${db_json}" | python3 -c "import json,sys; print(json.load(sys.stdin)['password'])")"
  export LOCAL_DB_HOST LOCAL_DB_PORT LOCAL_DB_NAME LOCAL_DB_USERNAME LOCAL_DB_PASSWORD
}

echo "Starting MiniStack..."
"${COMPOSE[@]}" up -d ministack

MINISTACK_CID="$("${COMPOSE[@]}" ps -q ministack)"
if [[ -z "${MINISTACK_CID}" ]]; then
  echo "Failed to get MiniStack container id." >&2
  exit 1
fi

echo "Waiting for MiniStack health..."
for _ in {1..60}; do
  STATUS="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "${MINISTACK_CID}" 2>/dev/null || true)"
  if [[ "${STATUS}" == "healthy" ]]; then
    break
  fi
  sleep 2
done

if [[ "${STATUS:-}" != "healthy" ]]; then
  echo "MiniStack did not become healthy in time." >&2
  exit 1
fi

echo "Waiting for Cognito bootstrap output..."
for _ in {1..60}; do
  if aws_in_ministack 'test -f /tmp/ministack/cognito-outputs.env' >/dev/null 2>&1; then
    break
  fi
  sleep 2
done

if ! aws_in_ministack 'test -f /tmp/ministack/cognito-outputs.env' >/dev/null 2>&1; then
  echo "MiniStack Cognito init output was not created in time." >&2
  exit 1
fi

echo "Waiting for RDS bootstrap output..."
for _ in {1..120}; do
  if aws_in_ministack 'test -f /tmp/ministack/db-outputs.env' >/dev/null 2>&1; then
    break
  fi
  sleep 2
done

if ! aws_in_ministack 'test -f /tmp/ministack/db-outputs.env' >/dev/null 2>&1; then
  echo "MiniStack RDS init output was not created in time. Check /tmp/ministack/ministack-init.log in the ministack container." >&2
  exit 1
fi

TMP_ENV_FILE="$(mktemp)"
trap 'rm -f "${TMP_ENV_FILE}"' EXIT

aws_in_ministack 'cat /tmp/ministack/cognito-outputs.env /tmp/ministack/db-outputs.env' >"${TMP_ENV_FILE}"

set -a
# shellcheck disable=SC1090
source "${TMP_ENV_FILE}"
set +a

if [[ -z "${LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON:-}" ]]; then
  LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON="$(
    aws_in_ministack "aws --endpoint-url http://localhost:4566 --region us-east-1 secretsmanager get-secret-value --secret-id socially/cognito/backend-client-secret --query SecretString --output text"
  )"
  export LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON
fi

if [[ -z "${LOCAL_DB_HOST:-}" ]]; then
  load_db_credentials_from_secret
fi

export LOCAL_COGNITO_REGION="${LOCAL_COGNITO_REGION:-us-east-1}"
export LOCAL_COGNITO_ISSUER_URL="http://ministack:4566/${LOCAL_COGNITO_USER_POOL_ID}"
export LOCAL_COGNITO_USE_MINISTACK="true"
export LOCAL_COGNITO_HOSTED_DOMAIN="${LOCAL_COGNITO_HOSTED_DOMAIN:-http://localhost:4566}"
export LOCAL_DB_HOST="${LOCAL_DB_HOST:-host.docker.internal}"
export LOCAL_DB_PORT="${LOCAL_DB_PORT:-15432}"

echo "Starting Backend with Cognito + RDS env wired from MiniStack..."
exec "${COMPOSE[@]}" up --build backend
