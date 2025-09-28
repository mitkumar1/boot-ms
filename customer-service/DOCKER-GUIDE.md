# 🐳 Docker Deployment Guide for Customer Service with Virtual Threads

This guide explains how to run your Customer Service application with Java 21 Virtual Threads using Docker.

## 📋 Prerequisites

- Docker Desktop installed and running
- Java 21 (for local development)
- Maven 3.8+ (for building)

## 🏗️ Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Start the application with all dependencies
docker-compose up -d

# View logs
docker-compose logs -f customer-service

# Stop everything
docker-compose down
```

### Option 2: Using Docker Commands

```bash
# Build the image
docker build -t customer-service:latest .

# Run the container
docker run -d \
  --name customer-service-app \
  -p 4040:4040 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseZGC --enable-preview" \
  -e VIRTUAL_THREAD_OPTS="-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256" \
  --restart unless-stopped \
  customer-service:latest
```

### Option 3: Using PowerShell Script (Windows)

```powershell
# Run the application
.\docker-run.ps1 run

# Run in development mode
.\docker-run.ps1 dev

# View logs
.\docker-run.ps1 logs

# Stop the application
.\docker-run.ps1 stop
```

### Option 4: Using Bash Script (Linux/Mac)

```bash
# Make script executable
chmod +x docker-run.sh

# Run the application
./docker-run.sh run

# Run in development mode
./docker-run.sh dev

# View logs
./docker-run.sh logs

# Stop the application
./docker-run.sh stop
```

## 🔧 Virtual Thread Configuration

The Docker setup includes optimized JVM flags for virtual threads:

```dockerfile
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseZGC --enable-preview"
ENV VIRTUAL_THREAD_OPTS="-Djdk.virtualThreadScheduler.parallelism=10 -Djdk.virtualThreadScheduler.maxPoolSize=256"
```

### JVM Flags Explained:

- `--enable-preview`: Enables preview features (required for virtual threads)
- `-XX:+UseZGC`: Uses ZGC garbage collector (optimal for virtual threads)
- `-Djdk.virtualThreadScheduler.parallelism=10`: Sets virtual thread scheduler parallelism
- `-Djdk.virtualThreadScheduler.maxPoolSize=256`: Sets maximum pool size for virtual threads

## 📊 Available Endpoints

Once the container is running, you can access:

### Application Endpoints
- **Health Check**: http://localhost:4040/actuator/health
- **Metrics**: http://localhost:4040/actuator/metrics
- **Scheduled Tasks**: http://localhost:4040/actuator/scheduledtasks

### Virtual Thread Monitoring
- **Thread Info**: http://localhost:4040/api/v1/virtual-threads/info
- **I/O Test**: http://localhost:4040/api/v1/virtual-threads/test-io
- **Bulk Operations**: http://localhost:4040/api/v1/virtual-threads/test-bulk
- **Performance Test**: http://localhost:4040/api/v1/virtual-threads/performance-test
- **Concurrency Test**: http://localhost:4040/api/v1/virtual-threads/test-concurrency
- **Thread Stats**: http://localhost:4040/api/v1/virtual-threads/stats

### Customer Service API
- **Get All Customers**: GET http://localhost:4040/api/v1/customers
- **Create Customer**: POST http://localhost:4040/api/v1/customers
- **Get Customer**: GET http://localhost:4040/api/v1/customers/{id}

## 🔍 Monitoring & Debugging

### View Application Logs
```bash
# Docker Compose
docker-compose logs -f customer-service

# Docker
docker logs -f customer-service-app
```

### Check Container Health
```bash
# Docker Compose
docker-compose ps

# Docker
docker ps --filter name=customer-service-app
```

### Health Check
```bash
curl http://localhost:4040/actuator/health
```

### Monitor Virtual Threads
```bash
# Get virtual thread information
curl http://localhost:4040/api/v1/virtual-threads/info

# Test virtual thread performance
curl http://localhost:4040/api/v1/virtual-threads/performance-test
```

## 🛠️ Development Mode

For development with debugging enabled:

```bash
# Using script
.\docker-run.ps1 dev

# Or manually
docker run -d \
  --name customer-service-app \
  -p 4040:4040 \
  -p 5005:5005 \
  -p 9999:9999 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseZGC --enable-preview -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  -v ./logs:/app/logs \
  customer-service:latest
```

**Debug Ports:**
- Application: 4040
- Java Debug: 5005
- JMX: 9999

## 🌐 Environment Profiles

The application supports different profiles:

### Docker Profile (Default)
- Optimized for container deployment
- H2 in-memory database
- Console logging

### Production Profile
```bash
docker run -d \
  --name customer-service-app \
  -p 4040:4040 \
  -e SPRING_PROFILES_ACTIVE=prod \
  customer-service:latest
```

### Development Profile
```bash
docker run -d \
  --name customer-service-app \
  -p 4040:4040 \
  -e SPRING_PROFILES_ACTIVE=dev \
  customer-service:latest
```

## 🧪 Testing Virtual Threads

### Test Scheduled Tasks
The application automatically runs scheduled tasks that demonstrate virtual threads:

```bash
# Check logs for scheduled task execution
docker logs customer-service-app | grep "THREAD-INFO"

# Example output:
# INFO c.k.m.service.ScheduledTaskService - 🧵 [THREAD-INFO] Health check running on: scheduled-task-1 (Virtual: true)
```

### Performance Testing
```bash
# Test I/O operations with virtual threads
curl -X GET "http://localhost:4040/api/v1/virtual-threads/test-io?operations=100"

# Test bulk operations
curl -X GET "http://localhost:4040/api/v1/virtual-threads/test-bulk?count=1000"

# Performance benchmark
curl -X GET "http://localhost:4040/api/v1/virtual-threads/performance-test?virtualThreads=1000&platformThreads=100"
```

## 🚀 Production Deployment

### Resource Limits
```yaml
# docker-compose.yml
services:
  customer-service:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 1G
        reservations:
          cpus: '1.0'
          memory: 512M
```

### Health Checks
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:4040/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

## 🔧 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Change the host port
   docker run -p 8080:4040 customer-service:latest
   ```

2. **Out of Memory**
   ```bash
   # Increase memory limit
   docker run -e JAVA_OPTS="-XX:MaxRAMPercentage=50.0" customer-service:latest
   ```

3. **Virtual Threads Not Working**
   ```bash
   # Check Java version in container
   docker exec customer-service-app java -version
   
   # Should show Java 21
   ```

### Debugging Commands
```bash
# Enter container shell
docker exec -it customer-service-app sh

# Check Java processes
docker exec customer-service-app jps -v

# Monitor JVM
docker exec customer-service-app jstat -gc 1
```

## 📈 Monitoring with Docker Compose

Start monitoring stack:
```bash
docker-compose --profile monitoring up -d
```

Access:
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

## 🛡️ Security

The Docker image follows security best practices:
- Non-root user (appuser:1001)
- Minimal Alpine Linux base
- No unnecessary packages
- Security labels

## 📝 Next Steps

1. **Scale the application**: Use Docker Swarm or Kubernetes
2. **Add external database**: Configure PostgreSQL or MySQL
3. **Implement service discovery**: Use Consul or Eureka
4. **Add distributed tracing**: Integrate Jaeger or Zipkin
5. **Setup CI/CD**: Use GitHub Actions or Jenkins

---

🎉 **Your Customer Service with Virtual Threads is now ready to run in Docker!**