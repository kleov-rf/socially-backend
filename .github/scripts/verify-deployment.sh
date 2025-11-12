#!/bin/bash
set -e

# Script to verify ECS deployment - fails if the deployed task definition doesn't match expected
# Usage: ./verify-deployment.sh --cluster <name> --service <name> --expected-task-def <arn>

# Parse named arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --cluster)
      CLUSTER_NAME="$2"
      shift 2
      ;;
    --service)
      SERVICE_NAME="$2"
      shift 2
      ;;
    --expected-task-def)
      EXPECTED_TASK_DEF="$2"
      shift 2
      ;;
    *)
      echo "Error: Unknown argument: $1"
      echo "Usage: $0 --cluster <name> --service <name> --expected-task-def <arn>"
      exit 1
      ;;
  esac
done

if [ -z "$CLUSTER_NAME" ] || [ -z "$SERVICE_NAME" ] || [ -z "$EXPECTED_TASK_DEF" ]; then
  echo "Error: Missing required arguments"
  echo "Usage: $0 --cluster <name> --service <name> --expected-task-def <arn>"
  exit 1
fi

echo "============================================"
echo "Verifying Deployment"
echo "============================================"
echo "Cluster: $CLUSTER_NAME"
echo "Service: $SERVICE_NAME"
echo "Expected Task Definition: $EXPECTED_TASK_DEF"
echo "============================================"

# Get the current task definition ARN used by the service
echo "Fetching current service configuration..."
CURRENT_TASK_DEF=$(aws ecs describe-services \
  --cluster "$CLUSTER_NAME" \
  --services "$SERVICE_NAME" \
  --query 'services[0].taskDefinition' \
  --output text)

if [ -z "$CURRENT_TASK_DEF" ] || [ "$CURRENT_TASK_DEF" == "None" ]; then
  echo "Error: Could not retrieve task definition for service $SERVICE_NAME"
  exit 1
fi

echo "Current Task Definition: $CURRENT_TASK_DEF"
echo "============================================"

# Compare the task definition ARNs
if [ "$CURRENT_TASK_DEF" == "$EXPECTED_TASK_DEF" ]; then
  echo "✅ SUCCESS: Deployment verified!"
  echo "The service is running the expected task definition."
  exit 0
else
  echo "❌ FAILURE: Deployment verification failed!"
  echo "Expected: $EXPECTED_TASK_DEF"
  echo "Actual:   $CURRENT_TASK_DEF"
  echo ""
  echo "This indicates a rollback has occurred or the deployment did not complete successfully."
  exit 1
fi
