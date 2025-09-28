@echo off
REM Quick Docker Run Script for Windows
REM This script builds and runs the customer service with virtual threads

echo 🐳 Customer Service Docker Quick Start
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Docker is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo ✅ Docker is running

REM Build the image
echo.
echo 🔨 Building Docker image...
docker build -t customer-service:latest .
if errorlevel 1 (
    echo ❌ Failed to build image
    pause
    exit /b 1
)

echo ✅ Image built successfully

REM Stop existing container if running
docker stop customer-service-app >nul 2>&1
docker rm customer-service-app >nul 2>&1

REM Run the container
echo.
echo 🚀 Starting container...
docker run -d ^
    --name customer-service-app ^
    -p 4040:4040 ^
    -e SPRING_PROFILES_ACTIVE=docker ^
    -e "JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseZGC --enable-preview" ^
    -e "VIRTUAL_THREAD_OPTS=-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256" ^
    --restart unless-stopped ^
    customer-service:latest

if errorlevel 1 (
    echo ❌ Failed to start container
    pause
    exit /b 1
)

echo ✅ Container started successfully
echo.
echo 🌐 Application available at: http://localhost:4040
echo 📊 Health check: http://localhost:4040/actuator/health
echo 🧵 Virtual threads: http://localhost:4040/api/v1/virtual-threads/info
echo.
echo 📋 View logs with: docker logs -f customer-service-app
echo ⏹️  Stop with: docker stop customer-service-app
echo.
pause