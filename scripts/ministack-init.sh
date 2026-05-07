#!/bin/sh
set -eu

export AWS_DEFAULT_REGION="${AWS_DEFAULT_REGION:-us-east-1}"
export AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID:-test}"
export AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY:-test}"

AWS_ENDPOINT="http://localhost:4566"
USER_POOL_NAME="socially-local"
SPA_CLIENT_NAME="socially-local-spa-client"
BACKEND_CLIENT_NAME="socially-local-backend-client"
BACKEND_SECRET_PATH="socially/cognito/backend-client-secret"
OUTPUT_FILE="/tmp/ministack/cognito-outputs.env"
# Hosted UI username/password sign-in (local dev only; override via ministack container env if needed).
LOCAL_DEV_USERNAME="${LOCAL_DEV_COGNITO_USERNAME:-dev@socially.local}"
LOCAL_DEV_PASSWORD="${LOCAL_DEV_COGNITO_PASSWORD:-SociallyDev1!}"

aws_local() {
  aws --endpoint-url "${AWS_ENDPOINT}" --region "${AWS_DEFAULT_REGION}" "$@"
}

is_empty() {
  [ -z "${1}" ] || [ "${1}" = "None" ] || [ "${1}" = "null" ]
}

wait_for_ministack() {
  attempts=0
  until aws_local sts get-caller-identity >/dev/null 2>&1; do
    attempts=$((attempts + 1))
    if [ "${attempts}" -ge 60 ]; then
      echo "MiniStack endpoint ${AWS_ENDPOINT} did not become reachable in time." >&2
      exit 1
    fi
    sleep 1
  done
}

ensure_local_dev_user() {
  pool_id="$1"
  if aws_local cognito-idp admin-get-user --user-pool-id "${pool_id}" --username "${LOCAL_DEV_USERNAME}" >/dev/null 2>&1; then
    return 0
  fi

  aws_local cognito-idp admin-create-user \
    --user-pool-id "${pool_id}" \
    --username "${LOCAL_DEV_USERNAME}" \
    --user-attributes "Name=email,Value=${LOCAL_DEV_USERNAME}" "Name=email_verified,Value=true" \
    --message-action SUPPRESS \
    --temporary-password "TempPassw0rd!" >/dev/null

  aws_local cognito-idp admin-set-user-password \
    --user-pool-id "${pool_id}" \
    --username "${LOCAL_DEV_USERNAME}" \
    --password "${LOCAL_DEV_PASSWORD}" \
    --permanent >/dev/null

  echo "MiniStack: created local dev user ${LOCAL_DEV_USERNAME} (see README / LOCAL_DEV_COGNITO_* env to customize)." >&2
}

provision_cognito() {
  wait_for_ministack

  POOL_ID="$(aws_local cognito-idp list-user-pools --max-results 60 --query "UserPools[?Name=='${USER_POOL_NAME}'].Id | [0]" --output text)"
  if is_empty "${POOL_ID}"; then
    POOL_ID="$(aws_local cognito-idp create-user-pool --pool-name "${USER_POOL_NAME}" --query "UserPool.Id" --output text)"
  fi

  SPA_CLIENT_ID="$(aws_local cognito-idp list-user-pool-clients --user-pool-id "${POOL_ID}" --max-results 60 --query "UserPoolClients[?ClientName=='${SPA_CLIENT_NAME}'].ClientId | [0]" --output text)"
  if is_empty "${SPA_CLIENT_ID}"; then
    SPA_CLIENT_ID="$(
      aws_local cognito-idp create-user-pool-client \
        --user-pool-id "${POOL_ID}" \
        --client-name "${SPA_CLIENT_NAME}" \
        --allowed-o-auth-flows-user-pool-client \
        --allowed-o-auth-flows code \
        --allowed-o-auth-scopes openid email profile \
        --supported-identity-providers COGNITO \
        --callback-urls '["http://localhost:5173/auth/callback/google"]' \
        --logout-urls '["http://localhost:5173"]' \
        --query "UserPoolClient.ClientId" \
        --output text
    )"
  fi

  BACKEND_CLIENT_ID="$(aws_local cognito-idp list-user-pool-clients --user-pool-id "${POOL_ID}" --max-results 60 --query "UserPoolClients[?ClientName=='${BACKEND_CLIENT_NAME}'].ClientId | [0]" --output text)"
  if is_empty "${BACKEND_CLIENT_ID}"; then
    BACKEND_CLIENT_ID="$(
      aws_local cognito-idp create-user-pool-client \
        --user-pool-id "${POOL_ID}" \
        --client-name "${BACKEND_CLIENT_NAME}" \
        --generate-secret \
        --explicit-auth-flows ALLOW_REFRESH_TOKEN_AUTH ALLOW_USER_PASSWORD_AUTH ALLOW_USER_SRP_AUTH \
        --query "UserPoolClient.ClientId" \
        --output text
    )"
  fi

  BACKEND_CLIENT_SECRET="$(aws_local cognito-idp describe-user-pool-client --user-pool-id "${POOL_ID}" --client-id "${BACKEND_CLIENT_ID}" --query "UserPoolClient.ClientSecret" --output text)"
  BACKEND_CLIENT_SECRET_JSON="$(printf '{"client_id":"%s","client_secret":"%s"}' "${BACKEND_CLIENT_ID}" "${BACKEND_CLIENT_SECRET}")"
  ESCAPED_BACKEND_CLIENT_SECRET_JSON="$(printf "%s" "${BACKEND_CLIENT_SECRET_JSON}" | sed "s/'/'\"'\"'/g")"

  if aws_local secretsmanager describe-secret --secret-id "${BACKEND_SECRET_PATH}" >/dev/null 2>&1; then
    aws_local secretsmanager put-secret-value --secret-id "${BACKEND_SECRET_PATH}" --secret-string "${BACKEND_CLIENT_SECRET_JSON}" >/dev/null
  else
    aws_local secretsmanager create-secret --name "${BACKEND_SECRET_PATH}" --secret-string "${BACKEND_CLIENT_SECRET_JSON}" >/dev/null
  fi

  ensure_local_dev_user "${POOL_ID}"

  mkdir -p "$(dirname "${OUTPUT_FILE}")"
  {
    printf "LOCAL_COGNITO_USER_POOL_ID=%s\n" "${POOL_ID}"
    printf "LOCAL_COGNITO_REGION=%s\n" "${AWS_DEFAULT_REGION}"
    printf "LOCAL_COGNITO_SPA_CLIENT_ID=%s\n" "${SPA_CLIENT_ID}"
    printf "LOCAL_COGNITO_BACKEND_CLIENT_ID=%s\n" "${BACKEND_CLIENT_ID}"
    printf "LOCAL_COGNITO_BACKEND_CLIENT_SECRET_JSON='%s'\n" "${ESCAPED_BACKEND_CLIENT_SECRET_JSON}"
  } >"${OUTPUT_FILE}"

  echo "MiniStack Cognito initialized."
  cat "${OUTPUT_FILE}"
}

if [ "${1:-}" != "--provision" ]; then
  mkdir -p "$(dirname "${OUTPUT_FILE}")"
  nohup sh "$0" --provision >/tmp/ministack/ministack-init.log 2>&1 &
  exit 0
fi

provision_cognito
