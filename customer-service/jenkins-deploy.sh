#!/bin/bash

# Jenkins CI/CD Deployment Script for Customer Service with Virtual Threads
# This script handles the deployment process called from Jenkins pipeline

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration from environment variables
DOCKER_IMAGE=${DOCKER_IMAGE:-"customer-service"}
DOCKER_TAG=${DOCKER_TAG:-"latest"}
DOCKER_REGISTRY=${DOCKER_REGISTRY:-"your-registry.com"}
APP_NAME=${APP_NAME:-"customer-service"}
NAMESPACE=${NAMESPACE:-"customer-service-prod"}
BUILD_NUMBER=${BUILD_NUMBER:-"1"}

# Function to print colored output
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Function to deploy with Jenkins
deploy_with_jenkins() {
    print_message $GREEN "🚀 Jenkins CI/CD Deployment Starting..."
    
    # Update deployment with new image
    local full_image="${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}"
    
    print_message $BLUE "📦 Deploying image: $full_image"
    
    # Apply Kubernetes manifests
    kubectl apply -f k8s/deployment-jenkins.yaml -n "$NAMESPACE"
    kubectl apply -f k8s/service-jenkins.yaml -n "$NAMESPACE"
    
    # Update image in deployment
    kubectl set image deployment/customer-service-jenkins \
        customer-service="$full_image" \
        -n "$NAMESPACE"
    
    # Wait for rollout
    kubectl rollout status deployment/customer-service-jenkins -n "$NAMESPACE" --timeout=600s
    
    print_message $GREEN "✅ Jenkins deployment completed!"
}

# Run deployment
deploy_with_jenkins