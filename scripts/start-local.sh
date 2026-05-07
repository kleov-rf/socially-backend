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
  if "${COMPOSE[@]}" exec -T ministack sh -lc 'test -f /tmp/ministack/cognito-outputs.env'; then
    break
  fi
  sleep 2
done

if ! "${COMPOSE[@]}" exec -T ministack sh -lc 'test -f /tmp/ministack/cognito-outputs.env'; then
  echo "MiniStack init output file was not created in time." >&2
  exit 1
fi

TMP_ENV_FILE="$(mktemp)"
trap 'rm -f "${TMP_ENV_FILE}"' EXIT

"${COMPOSE[@]}" exec -T ministack sh -lc 'cat /tmp/ministack/cognito-outputs.env' >"${TMP_ENV_FILE}"

set -a
source "${TMP_ENV_FILE}"
set +a

if [[ -z "${LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON:-}" ]]; then
  LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON="$(
    "${COMPOSE[@]}" exec -T ministack sh -lc "aws --endpoint-url http://localhost:4566 --region us-east-1 secretsmanager get-secret-value --secret-id socially/cognito/backend-client-secret --query SecretString --output text"
  )"
  export LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON
fi

export LOCAL_COGNITO_REGION="${LOCAL_COGNITO_REGION:-us-east-1}"
export LOCAL_COGNITO_ISSUER_URL="http://ministack:4566/${LOCAL_COGNITO_USER_POOL_ID}"
export LOCAL_COGNITO_USE_MINISTACK="true"
# SPA redirects run in the browser (localhost). Token exchange base URL: application-local.yaml (auth.oauth.base-url).
export LOCAL_COGNITO_HOSTED_DOMAIN="${LOCAL_COGNITO_HOSTED_DOMAIN:-http://localhost:4566}"

echo "Starting Postgres + Backend with Cognito env wired from MiniStack..."
exec "${COMPOSE[@]}" up --build postgres backend
