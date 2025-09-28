# 🐳 Docker Deployment Guide

This guide explains how to run the Customer Service application using Docker and Docker Compose.

## 📋 Prerequisites

- **Docker Desktop** installed and running
- **Docker Compose** (included with Docker Desktop)
- **Maven** (for building the application)
- **Java 21** (for local development)

## 🚀 Quick Start

### Option 1: Using PowerShell Script (Windows - Recommended)

```powershell
# Build and run with docker-compose
.\docker-run.ps1 run

# Or run standalone container
.\docker-run.ps1 standalone

# View logs
.\docker-run.ps1 logs

# Stop services
.\docker-run.ps1 stop
```

### Option 2: Using Bash Script (Linux/Mac)

```bash
# Make script executable
chmod +x docker-run.sh

# Build and run with docker-compose
./docker-run.sh run

# Or run standalone container
./docker-run.sh standalone

# View logs
./docker-run.sh logs

# Stop services
./docker-run.sh stop
```

### Option 3: Manual Docker Commands

#### Build the Application
```bash
# Build Spring Boot JAR
mvn clean package -DskipTests

# Build Docker image
docker build -t customer-service:latest .
```

#### Run with Docker Compose (Recommended)
```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f customer-service

# Stop services
docker-compose down
```

#### Run Standalone Container
```bash
# Run container
docker run -d \
  --name customer-service-app \
  -p 4040:4040 \
  -e SPRING_PROFILES_ACTIVE=docker \
  customer-service:latest

# Check logs
docker logs -f customer-service-app

# Stop container
docker stop customer-service-app
docker rm customer-service-app
```

## 🔗 Application URLs

Once the application is running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| **Main Application** | http://localhost:4040 | Customer Service API |
| **Health Check** | http://localhost:4040/actuator/health | Application health status |
| **H2 Database Console** | http://localhost:4040/h2-console | Database management |
| **Circuit Breakers** | http://localhost:4040/actuator/circuitbreakers | Resilience4j circuit breaker status |
| **Metrics** | http://localhost:4040/actuator/metrics | Application metrics |
| **Config Server** | http://localhost:8888 | Spring Cloud Config Server |

### H2 Database Connection Details
- **JDBC URL**: `jdbc:h2:mem:customer_data`
- **Username**: `sa`
- **Password**: *(empty)*

## 📡 API Endpoints

### Customer Management
```bash
# Get all customers
curl http://localhost:4040/api/v1/customers

# Create a customer
curl -X POST http://localhost:4040/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"customerName":"John Doe","email":"john@example.com","mobileNumber":9876543210}'

# Get customer by ID
curl http://localhost:4040/api/v1/customers/1
```

### Resilience4j Endpoints
```bash
# Test circuit breaker with Kubernetes service
curl http://localhost:4040/api/v1/resilience/k8s/customer-service/health

# Test external service call
curl http://localhost:4040/api/v1/resilience/external/httpbin.org/get

# Check circuit breaker status
curl http://localhost:4040/actuator/circuitbreakers
```

## 🏗️ Docker Architecture

### Multi-Stage Build
The Dockerfile uses a multi-stage build for optimal image size:

1. **Builder Stage**: Uses `eclipse-temurin:21-jdk-alpine` to compile the application
2. **Runtime Stage**: Uses `eclipse-temurin:21-jre-alpine` for the final lightweight image

### Security Features
- ✅ Non-root user (`appuser`)
- ✅ Minimal Alpine Linux base
- ✅ Security scanning friendly
- ✅ Container-optimized JVM settings

### Health Checks
- ✅ Built-in health check using Spring Boot Actuator
- ✅ 30-second intervals with 3 retries
- ✅ 60-second startup grace period

## 🎛️ Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `docker` | Active Spring profile |
| `JAVA_OPTS` | Container optimized | JVM options |
| `JMX_OPTS` | JMX disabled | JMX configuration |

### Production Environment Variables
```bash
# Performance tuning
JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Enable JMX monitoring
JMX_OPTS="-Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=1099"

# External config server
SPRING_CLOUD_CONFIG_URI=http://config-server:8888
```

## 📊 Monitoring Setup

### Start with Monitoring Stack
```bash
# Start with Prometheus and Grafana
docker-compose --profile monitoring up -d

# Access Grafana at http://localhost:3000
# Default credentials: admin/admin
```

### Available Metrics
- **Application Metrics**: JVM, HTTP requests, database connections
- **Resilience4j Metrics**: Circuit breaker states, retry attempts, rate limiting
- **Custom Business Metrics**: Customer operations, API response times

## 🐛 Troubleshooting

### Check Application Status
```bash
# Check if container is running
docker ps | grep customer-service

# Check application health
curl http://localhost:4040/actuator/health

# View detailed logs
docker logs -f customer-service-app

# Check resource usage
docker stats customer-service-app
```

### Common Issues

#### Port Already in Use
```bash
# Find process using port 4040
netstat -ano | findstr :4040  # Windows
lsof -i :4040                 # Linux/Mac

# Kill the process or use different port
docker run -p 4041:4040 customer-service:latest
```

#### Application Won't Start
```bash
# Check logs for startup errors
docker logs customer-service-app

# Common issues:
# - Insufficient memory (increase container memory)
# - Port conflicts (use different port mapping)
# - Configuration errors (check application-docker.yml)
```

#### Memory Issues
```bash
# Increase container memory limit
docker run --memory=512m customer-service:latest

# Or adjust JVM heap size
docker run -e "JAVA_OPTS=-Xmx256m" customer-service:latest
```

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
name: Docker Build and Deploy
on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build and Push Docker Image
        run: |
          docker build -t customer-service:${{ github.sha }} .
          docker tag customer-service:${{ github.sha }} customer-service:latest
```

### Production Deployment
```bash
# Build production image
docker build -t customer-service:v1.0.0 .

# Tag for registry
docker tag customer-service:v1.0.0 your-registry/customer-service:v1.0.0

# Push to registry
docker push your-registry/customer-service:v1.0.0
```

## 📚 Additional Resources

- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Resilience4j Metrics](https://resilience4j.readme.io/docs/micrometer)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)

---

## 🎯 Next Steps

1. **Production Database**: Replace H2 with PostgreSQL or MySQL
2. **External Config**: Set up Spring Cloud Config Server
3. **Service Discovery**: Implement Kubernetes service discovery
4. **Monitoring**: Add comprehensive observability stack
5. **Security**: Implement OAuth2/JWT authentication