# Customer Service Docker Runner for Windows
# This script builds and runs the customer service using Docker

param(
    [Parameter(Position=0)]
    [ValidateSet("build", "run", "standalone", "stop", "logs", "clean", "help")]
    [string]$Action = "help"
)

# Function to print colored output
function Write-Status {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Function to check if Docker is running
function Test-Docker {
    try {
        docker info | Out-Null
        Write-Success "Docker is running"
        return $true
    }
    catch {
        Write-Error "Docker is not running. Please start Docker and try again."
        return $false
    }
}

# Function to build the application
function Build-App {
    Write-Status "Building Spring Boot application..."
    try {
        mvn clean package -DskipTests
        Write-Success "Application built successfully"
        return $true
    }
    catch {
        Write-Error "Failed to build application"
        return $false
    }
}

# Function to build Docker image
function Build-Image {
    Write-Status "Building Docker image..."
    try {
        docker build -t customer-service:latest .
        Write-Success "Docker image built successfully"
        return $true
    }
    catch {
        Write-Error "Failed to build Docker image"
        return $false
    }
}

# Function to run with docker-compose
function Start-Compose {
    Write-Status "Starting services with docker-compose..."
    docker-compose up -d
    Write-Success "Services started successfully"
    
    Write-Status "Waiting for services to be healthy..."
    Start-Sleep -Seconds 30
    
    Write-Status "Checking service health..."
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:4040/actuator/health" -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Success "Customer service is healthy!"
            Write-Status "Application is available at: http://localhost:4040"
            Write-Status "H2 Console available at: http://localhost:4040/h2-console"
            Write-Status "Health check: http://localhost:4040/actuator/health"
            Write-Status "Circuit breakers: http://localhost:4040/actuator/circuitbreakers"
        }
    }
    catch {
        Write-Warning "Service might still be starting up. Check logs with: docker-compose logs customer-service"
    }
}

# Function to run standalone Docker container
function Start-Standalone {
    Write-Status "Running standalone Docker container..."
    
    # Stop and remove existing container if running
    try {
        docker stop customer-service-app | Out-Null
        docker rm customer-service-app | Out-Null
    }
    catch {
        # Container might not exist, continue
    }
    
    # Run the container
    docker run -d `
        --name customer-service-app `
        -p 4040:4040 `
        -e SPRING_PROFILES_ACTIVE=docker `
        -e "JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" `
        customer-service:latest
    
    Write-Success "Container started successfully"
    
    Write-Status "Waiting for application to start..."
    Start-Sleep -Seconds 30
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:4040/actuator/health" -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Success "Customer service is healthy!"
            Write-Status "Application is available at: http://localhost:4040"
        }
    }
    catch {
        Write-Warning "Service might still be starting up. Check logs with: docker logs customer-service-app"
    }
}

# Function to show usage
function Show-Usage {
    Write-Host "Usage: .\docker-run.ps1 [ACTION]" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Actions:" -ForegroundColor Cyan
    Write-Host "  build      - Build the application and Docker image"
    Write-Host "  run        - Run with docker-compose (recommended)"
    Write-Host "  standalone - Run standalone Docker container"
    Write-Host "  stop       - Stop all services"
    Write-Host "  logs       - Show logs"
    Write-Host "  clean      - Clean up containers and images"
    Write-Host "  help       - Show this help message"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\docker-run.ps1 build              # Build application and image"
    Write-Host "  .\docker-run.ps1 run                # Start with docker-compose"
    Write-Host "  .\docker-run.ps1 standalone         # Run standalone container"
    Write-Host "  .\docker-run.ps1 logs               # Show application logs"
}

# Function to stop services
function Stop-Services {
    Write-Status "Stopping services..."
    try { docker-compose down | Out-Null } catch {}
    try { docker stop customer-service-app | Out-Null } catch {}
    try { docker rm customer-service-app | Out-Null } catch {}
    Write-Success "Services stopped"
}

# Function to show logs
function Show-Logs {
    $containers = docker ps --format "{{.Names}}" | Where-Object { $_ -match "customer-service" }
    if ($containers) {
        $composeContainers = docker-compose ps --services 2>$null | Where-Object { $_ -match "customer-service" }
        if ($composeContainers) {
            docker-compose logs -f customer-service
        } else {
            docker logs -f customer-service-app
        }
    } else {
        Write-Warning "No customer service container is running"
    }
}

# Function to clean up
function Clear-All {
    Write-Status "Cleaning up containers and images..."
    try { docker-compose down --rmi all --volumes --remove-orphans | Out-Null } catch {}
    try { docker stop customer-service-app | Out-Null } catch {}
    try { docker rm customer-service-app | Out-Null } catch {}
    try { docker rmi customer-service:latest | Out-Null } catch {}
    Write-Success "Cleanup completed"
}

# Main script logic
switch ($Action) {
    "build" {
        if (Test-Docker) {
            if (Build-App) {
                Build-Image | Out-Null
            }
        }
    }
    "run" {
        if (Test-Docker) {
            if (Build-App) {
                if (Build-Image) {
                    Start-Compose
                }
            }
        }
    }
    "standalone" {
        if (Test-Docker) {
            if (Build-App) {
                if (Build-Image) {
                    Start-Standalone
                }
            }
        }
    }
    "stop" {
        Stop-Services
    }
    "logs" {
        Show-Logs
    }
    "clean" {
        Stop-Services
        Clear-All
    }
    default {
        Show-Usage
    }
}