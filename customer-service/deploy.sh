#!/bin/bash

# Kubernetes Deployment Script for Customer Service
# This script deploys the customer service with all Kubernetes resources

set -e

# Configuration
NAMESPACE="${NAMESPACE:-default}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
REGISTRY="${REGISTRY:-your-registry.com}"
APP_NAME="customer-service"

echo "🚀 Deploying Customer Service to Kubernetes..."
echo "Namespace: $NAMESPACE"
echo "Image Tag: $IMAGE_TAG"
echo "Registry: $REGISTRY"

# Function to check if kubectl is available
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        echo "❌ kubectl is not installed or not in PATH"
        exit 1
    fi
    echo "✅ kubectl is available"
}

# Function to check cluster connection
check_cluster() {
    if ! kubectl cluster-info &> /dev/null; then
        echo "❌ Cannot connect to Kubernetes cluster"
        exit 1
    fi
    echo "✅ Connected to Kubernetes cluster"
}

# Function to create namespace if it doesn't exist
ensure_namespace() {
    if ! kubectl get namespace "$NAMESPACE" &> /dev/null; then
        echo "📝 Creating namespace: $NAMESPACE"
        kubectl create namespace "$NAMESPACE"
    else
        echo "✅ Namespace $NAMESPACE already exists"
    fi
}

# Function to build and push Docker image
build_and_push_image() {
    echo "🔨 Building Docker image..."
    docker build -t "$REGISTRY/$APP_NAME:$IMAGE_TAG" .
    
    echo "📤 Pushing image to registry..."
    docker push "$REGISTRY/$APP_NAME:$IMAGE_TAG"
    
    echo "✅ Image built and pushed successfully"
}

# Function to update image tags in Kubernetes manifests
update_image_tags() {
    echo "📝 Updating image tags in manifests..."
    
    # Update deployment.yaml with the correct image
    sed -i "s|image: customer-service:latest|image: $REGISTRY/$APP_NAME:$IMAGE_TAG|g" k8s/deployment.yaml
    
    echo "✅ Image tags updated"
}

# Function to apply Kubernetes resources
deploy_resources() {
    echo "📦 Deploying Kubernetes resources..."
    
    # Apply resources in order
    kubectl apply -f k8s/configmap.yaml -n "$NAMESPACE"
    kubectl apply -f k8s/deployment.yaml -n "$NAMESPACE"
    kubectl apply -f k8s/service.yaml -n "$NAMESPACE"
    kubectl apply -f k8s/ingress.yaml -n "$NAMESPACE"
    kubectl apply -f k8s/autoscaling.yaml -n "$NAMESPACE"
    
    echo "✅ Resources deployed successfully"
}

# Function to wait for deployment to be ready
wait_for_deployment() {
    echo "⏳ Waiting for deployment to be ready..."
    
    kubectl rollout status deployment/$APP_NAME -n "$NAMESPACE" --timeout=300s
    
    echo "✅ Deployment is ready"
}

# Function to run health checks
health_check() {
    echo "🔍 Running health checks..."
    
    # Get service IP
    SERVICE_IP=$(kubectl get service $APP_NAME -n "$NAMESPACE" -o jsonpath='{.spec.clusterIP}')
    
    # Check if pods are running
    RUNNING_PODS=$(kubectl get pods -n "$NAMESPACE" -l app=$APP_NAME --field-selector=status.phase=Running --no-headers | wc -l)
    
    echo "Service IP: $SERVICE_IP"
    echo "Running Pods: $RUNNING_PODS"
    
    if [ "$RUNNING_PODS" -gt 0 ]; then
        echo "✅ Health check passed"
    else
        echo "❌ Health check failed"
        exit 1
    fi
}

# Function to show deployment info
show_deployment_info() {
    echo ""
    echo "📊 Deployment Information:"
    echo "=========================="
    
    echo ""
    echo "Pods:"
    kubectl get pods -n "$NAMESPACE" -l app=$APP_NAME
    
    echo ""
    echo "Services:"
    kubectl get services -n "$NAMESPACE" -l app=$APP_NAME
    
    echo ""
    echo "Ingress:"
    kubectl get ingress -n "$NAMESPACE" -l app=$APP_NAME
    
    echo ""
    echo "HPA:"
    kubectl get hpa -n "$NAMESPACE" -l app=$APP_NAME
    
    echo ""
    echo "🎉 Deployment completed successfully!"
    echo ""
    echo "Access the application at:"
    echo "- Internal: http://$APP_NAME.$NAMESPACE.svc.cluster.local"
    echo "- External: Check ingress configuration for external access"
    echo ""
    echo "Monitor the application:"
    echo "- Metrics: http://<ingress-host>/actuator/prometheus"
    echo "- Health: http://<ingress-host>/actuator/health"
    echo "- Circuit Breakers: http://<ingress-host>/actuator/circuitbreakers"
}

# Function to cleanup deployment
cleanup() {
    echo "🧹 Cleaning up deployment..."
    
    kubectl delete -f k8s/ -n "$NAMESPACE" --ignore-not-found=true
    
    echo "✅ Cleanup completed"
}

# Main deployment flow
main() {
    case "${1:-deploy}" in
        "deploy")
            check_kubectl
            check_cluster
            ensure_namespace
            
            # Skip build if NO_BUILD is set
            if [[ -z "$NO_BUILD" ]]; then
                build_and_push_image
            fi
            
            update_image_tags
            deploy_resources
            wait_for_deployment
            health_check
            show_deployment_info
            ;;
        "cleanup")
            check_kubectl
            check_cluster
            cleanup
            ;;
        "status")
            check_kubectl
            check_cluster
            show_deployment_info
            ;;
        *)
            echo "Usage: $0 [deploy|cleanup|status]"
            echo ""
            echo "Commands:"
            echo "  deploy  - Deploy the application (default)"
            echo "  cleanup - Remove all resources"
            echo "  status  - Show deployment status"
            echo ""
            echo "Environment Variables:"
            echo "  NAMESPACE   - Kubernetes namespace (default: default)"
            echo "  IMAGE_TAG   - Docker image tag (default: latest)"
            echo "  REGISTRY    - Docker registry (default: your-registry.com)"
            echo "  NO_BUILD    - Skip Docker build and push"
            exit 1
            ;;
    esac
}

# Run main function
main "$@"