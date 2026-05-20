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
DB_INSTANCE_ID="socially-local"
DB_ENGINE_VERSION="18.2"
DB_NAME="socially"
DB_MASTER_USERNAME="socially_admin"
DB_MASTER_PASSWORD="${LOCAL_RDS_MASTER_PASSWORD:-LocalDevPass1!}"
DB_SECRET_PATH="socially/db/credentials"
DB_OUTPUT_FILE="/tmp/ministack/db-outputs.env"
RDS_HOST_PORT="${RDS_BASE_PORT:-15432}"
LOCAL_RDS_CLIENT_HOST="${LOCAL_RDS_CLIENT_HOST:-host.docker.internal}"
# Hosted UI username/password sign-in (local dev only; override via ministack container env if needed).
LOCAL_DEV_USERNAME="${LOCAL_DEV_COGNITO_USERNAME:-dev@socially.local}"
LOCAL_DEV_PASSWORD="${LOCAL_DEV_COGNITO_PASSWORD:-SociallyDev1!}"
LOCAL_DEV_GIVEN_NAME="${LOCAL_DEV_COGNITO_GIVEN_NAME:-Dev}"
LOCAL_DEV_FAMILY_NAME="${LOCAL_DEV_COGNITO_FAMILY_NAME:-User}"

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
    --user-attributes \
      "Name=email,Value=${LOCAL_DEV_USERNAME}" \
      "Name=email_verified,Value=true" \
      "Name=given_name,Value=${LOCAL_DEV_GIVEN_NAME}" \
      "Name=family_name,Value=${LOCAL_DEV_FAMILY_NAME}" \
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

wait_for_rds_available() {
  attempts=0
  while true; do
    STATUS="$(aws_local rds describe-db-instances --db-instance-identifier "${DB_INSTANCE_ID}" --query "DBInstances[0].DBInstanceStatus" --output text 2>/dev/null || true)"
    if [ "${STATUS}" = "available" ]; then
      return 0
    fi
    attempts=$((attempts + 1))
    if [ "${attempts}" -ge 120 ]; then
      echo "RDS instance ${DB_INSTANCE_ID} did not become available in time (last status: ${STATUS})." >&2
      exit 1
    fi
    sleep 2
  done
}

provision_rds() {
  wait_for_ministack

  if ! aws_local rds describe-db-instances --db-instance-identifier "${DB_INSTANCE_ID}" >/dev/null 2>&1; then
    aws_local rds create-db-instance \
      --db-instance-identifier "${DB_INSTANCE_ID}" \
      --db-instance-class db.t3.micro \
      --engine postgres \
      --engine-version "${DB_ENGINE_VERSION}" \
      --master-username "${DB_MASTER_USERNAME}" \
      --master-user-password "${DB_MASTER_PASSWORD}" \
      --db-name "${DB_NAME}" \
      --allocated-storage 20 >/dev/null
  fi

  wait_for_rds_available

  DB_SECRET_JSON="$(printf '{"username":"%s","password":"%s","host":"%s","port":"%s","dbname":"%s"}' \
    "${DB_MASTER_USERNAME}" \
    "${DB_MASTER_PASSWORD}" \
    "${LOCAL_RDS_CLIENT_HOST}" \
    "${RDS_HOST_PORT}" \
    "${DB_NAME}")"

  if aws_local secretsmanager describe-secret --secret-id "${DB_SECRET_PATH}" >/dev/null 2>&1; then
    aws_local secretsmanager put-secret-value --secret-id "${DB_SECRET_PATH}" --secret-string "${DB_SECRET_JSON}" >/dev/null
  else
    aws_local secretsmanager create-secret --name "${DB_SECRET_PATH}" --secret-string "${DB_SECRET_JSON}" >/dev/null
  fi

  mkdir -p "$(dirname "${DB_OUTPUT_FILE}")"
  {
    printf "LOCAL_DB_HOST=%s\n" "${LOCAL_RDS_CLIENT_HOST}"
    printf "LOCAL_DB_PORT=%s\n" "${RDS_HOST_PORT}"
    printf "LOCAL_DB_NAME=%s\n" "${DB_NAME}"
    printf "LOCAL_DB_USERNAME=%s\n" "${DB_MASTER_USERNAME}"
    printf "LOCAL_DB_PASSWORD=%s\n" "${DB_MASTER_PASSWORD}"
  } >"${DB_OUTPUT_FILE}"

  echo "MiniStack RDS initialized (postgres ${DB_ENGINE_VERSION}, host port ${RDS_HOST_PORT})."
  cat "${DB_OUTPUT_FILE}"
}

provision_s3() {
  wait_for_ministack
  MEDIA_BUCKET="${LOCAL_MEDIA_STORAGE_S3_BUCKET:-socially-media}"

  if aws_local s3api head-bucket --bucket "${MEDIA_BUCKET}" 2>/dev/null; then
    echo "MiniStack S3: bucket ${MEDIA_BUCKET} already exists." >&2
    return 0
  fi

  aws_local s3 mb "s3://${MEDIA_BUCKET}" >/dev/null
  echo "MiniStack S3: created bucket ${MEDIA_BUCKET}." >&2
}

if [ "${1:-}" != "--provision" ]; then
  mkdir -p "$(dirname "${OUTPUT_FILE}")" "$(dirname "${DB_OUTPUT_FILE}")"
  nohup sh "$0" --provision >/tmp/ministack/ministack-init.log 2>&1 &
  exit 0
fi

provision_s3
provision_cognito
provision_rds
