#!/bin/bash

# Customer Service Docker Runner
# This script builds and runs the customer service using Docker

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to build the application
build_app() {
    print_status "Building Spring Boot application..."
    if mvn clean package -DskipTests; then
        print_success "Application built successfully"
    else
        print_error "Failed to build application"
        exit 1
    fi
}

# Function to build Docker image
build_image() {
    print_status "Building Docker image..."
    if docker build -t customer-service:latest .; then
        print_success "Docker image built successfully"
    else
        print_error "Failed to build Docker image"
        exit 1
    fi
}

# Function to run with docker-compose
run_compose() {
    print_status "Starting services with docker-compose..."
    docker-compose up -d
    print_success "Services started successfully"
    
    print_status "Waiting for services to be healthy..."
    sleep 30
    
    print_status "Checking service health..."
    if curl -f http://localhost:4040/actuator/health >/dev/null 2>&1; then
        print_success "Customer service is healthy!"
        print_status "Application is available at: http://localhost:4040"
        print_status "H2 Console available at: http://localhost:4040/h2-console"
        print_status "Health check: http://localhost:4040/actuator/health"
        print_status "Circuit breakers: http://localhost:4040/actuator/circuitbreakers"
    else
        print_warning "Service might still be starting up. Check logs with: docker-compose logs customer-service"
    fi
}

# Function to run standalone Docker container
run_standalone() {
    print_status "Running standalone Docker container..."
    
    # Stop and remove existing container if running
    docker stop customer-service-app 2>/dev/null || true
    docker rm customer-service-app 2>/dev/null || true
    
    # Run the container
    docker run -d \
        --name customer-service-app \
        -p 4040:4040 \
        -e SPRING_PROFILES_ACTIVE=docker \
        -e JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
        customer-service:latest
    
    print_success "Container started successfully"
    
    print_status "Waiting for application to start..."
    sleep 30
    
    if curl -f http://localhost:4040/actuator/health >/dev/null 2>&1; then
        print_success "Customer service is healthy!"
        print_status "Application is available at: http://localhost:4040"
    else
        print_warning "Service might still be starting up. Check logs with: docker logs customer-service-app"
    fi
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  build     - Build the application and Docker image"
    echo "  run       - Run with docker-compose (recommended)"
    echo "  standalone - Run standalone Docker container"
    echo "  stop      - Stop all services"
    echo "  logs      - Show logs"
    echo "  clean     - Clean up containers and images"
    echo "  help      - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 build              # Build application and image"
    echo "  $0 run                # Start with docker-compose"
    echo "  $0 standalone         # Run standalone container"
    echo "  $0 logs               # Show application logs"
}

# Function to stop services
stop_services() {
    print_status "Stopping services..."
    docker-compose down 2>/dev/null || true
    docker stop customer-service-app 2>/dev/null || true
    docker rm customer-service-app 2>/dev/null || true
    print_success "Services stopped"
}

# Function to show logs
show_logs() {
    if docker ps | grep -q customer-service; then
        if docker-compose ps | grep -q customer-service; then
            docker-compose logs -f customer-service
        else
            docker logs -f customer-service-app
        fi
    else
        print_warning "No customer service container is running"
    fi
}

# Function to clean up
clean_up() {
    print_status "Cleaning up containers and images..."
    docker-compose down --rmi all --volumes --remove-orphans 2>/dev/null || true
    docker stop customer-service-app 2>/dev/null || true
    docker rm customer-service-app 2>/dev/null || true
    docker rmi customer-service:latest 2>/dev/null || true
    print_success "Cleanup completed"
}

# Main script logic
case "${1:-help}" in
    build)
        check_docker
        build_app
        build_image
        ;;
    run)
        check_docker
        build_app
        build_image
        run_compose
        ;;
    standalone)
        check_docker
        build_app
        build_image
        run_standalone
        ;;
    stop)
        stop_services
        ;;
    logs)
        show_logs
        ;;
    clean)
        stop_services
        clean_up
        ;;
    help|*)
        show_usage
        ;;
esac