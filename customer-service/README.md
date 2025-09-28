# Customer Service - Cloud-Native Microservice

A modern, cloud-native microservice built with Spring Boot 3.4.0, featuring comprehensive resilience patterns, Kubernetes integration, and Spring Cloud Config.

## 🏗️ Architecture

- **Framework**: Spring Boot 3.4.0 with Java 21
- **Virtual Threads**: Project Loom integration for high-concurrency I/O operations
- **Scheduling**: Spring Scheduling with virtual thread support for background tasks
- **Resilience**: Resilience4j (Circuit Breaker, Bulkhead, Rate Limiter, Retry, Time Limiter)
- **Configuration**: Spring Cloud Config with bootstrap configuration
- **Service Discovery**: Kubernetes DNS-based service discovery
- **Database**: H2 (development), JPA/Hibernate
- **Monitoring**: Spring Boot Actuator with health checks and metrics
- **Containerization**: Docker with multi-stage builds

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **Docker** (for containerized deployment)
- **Git**

### Local Development

```bash
# Clone and navigate to the project
cd customer-service

# Run the application
mvn spring-boot:run

# Application will be available at http://localhost:4040
```

### Docker Deployment

#### Option 1: Using PowerShell Script (Windows - Recommended)

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

#### Option 2: Using Bash Script (Linux/Mac)

```bash
# Make script executable
chmod +x docker-run.sh

# Build and run
./docker-run.sh run
```

#### Option 3: Manual Docker Commands

```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t customer-service:latest .

# Run with docker-compose (recommended)
docker-compose up -d

# Or run standalone
docker run -d --name customer-service-app -p 4040:4040 customer-service:latest
```

## 📡 API Endpoints

### Customer Management

```bash
# Get all customers
GET http://localhost:4040/api/v1/customers

# Create a customer
POST http://localhost:4040/api/v1/customers
Content-Type: application/json
{
  "customerName": "John Doe",
  "email": "john@example.com",
  "mobileNumber": 9876543210
}

# Get customer by ID
GET http://localhost:4040/api/v1/customers/{id}

# Update customer
PUT http://localhost:4040/api/v1/customers/{id}

# Delete customer
DELETE http://localhost:4040/api/v1/customers/{id}
```

### Resilience4j Endpoints

```bash
# Test circuit breaker with Kubernetes service
GET http://localhost:4040/api/v1/resilience/k8s/customer-service/health

# Test external service call
GET http://localhost:4040/api/v1/resilience/external/httpbin.org/get

# Async call with timeout
GET http://localhost:4040/api/v1/resilience/async/{serviceName}/{endpoint}
```

### Virtual Thread Endpoints

```bash
# Get current thread information
GET http://localhost:4040/api/v1/virtual-threads/info

# Test I/O intensive operation
GET http://localhost:4040/api/v1/virtual-threads/test-io/1000

# Execute async task
POST http://localhost:4040/api/v1/virtual-threads/async-task
Content-Type: application/json
{"processingTimeMs": 2000}

# Test bulk operations with virtual threads
POST http://localhost:4040/api/v1/virtual-threads/bulk-test/100

# Performance test
GET http://localhost:4040/api/v1/virtual-threads/performance-test/1000

# Concurrency test
GET http://localhost:4040/api/v1/virtual-threads/concurrent-test/50

# Get thread statistics
GET http://localhost:4040/api/v1/virtual-threads/stats
```

### Monitoring & Health

```bash
# Health check
GET http://localhost:4040/actuator/health

# Circuit breaker status
GET http://localhost:4040/actuator/circuitbreakers

# Scheduled tasks status
GET http://localhost:4040/actuator/scheduledtasks

# Application metrics
GET http://localhost:4040/actuator/metrics

# All actuator endpoints
GET http://localhost:4040/actuator
```

## 🔧 Configuration

### Application Profiles

- **default**: Local development with H2 database
- **docker**: Docker container configuration
- **kubernetes**: Kubernetes deployment configuration

### Key Configuration Files

```
src/main/resources/
├── application.yml              # Default configuration
├── application-docker.yml       # Docker-specific config
├── application-standalone.yml   # Standalone config
└── bootstrap.yml               # Spring Cloud Config bootstrap
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `default` | Active Spring profile |
| `SERVER_PORT` | `4040` | Application port |
| `JAVA_OPTS` | Container optimized | JVM options |
| `SPRING_CLOUD_CONFIG_URI` | `http://localhost:8888` | Config server URL |

## 🛡️ Resilience Patterns

### Circuit Breaker
- **Default**: 50% failure rate, 30s open state
- **External Service**: 60% failure rate, 60s open state  
- **Kubernetes Service**: 40% failure rate, 45s open state

### Retry Policy
- **Default**: 3 attempts, 2s wait
- **External Service**: 5 attempts, exponential backoff

### Rate Limiting
- **Default**: 100 requests/second
- **External Service**: 50 requests/second

### Bulkhead
- **Default**: 50 concurrent calls
- **External Service**: 25 concurrent calls

### Time Limiter
- **Default**: 10s timeout
- **External Service**: 5s timeout

## 🐳 Docker

### Multi-Stage Build

The Dockerfile uses a multi-stage build for optimal image size:
- **Builder Stage**: `eclipse-temurin:21-jdk-alpine` for compilation
- **Runtime Stage**: `eclipse-temurin:21-jre-alpine` for execution

### Security Features
- ✅ Non-root user (`appuser`)
- ✅ Minimal Alpine Linux base
- ✅ Container-optimized JVM settings
- ✅ Health checks with retries

### Available Services

| Service | Port | Description |
|---------|------|-------------|
| **Customer Service** | 4040 | Main application |
| **Config Server** | 8888 | Spring Cloud Config |
| **Prometheus** | 9090 | Metrics collection |
| **Grafana** | 3000 | Monitoring dashboard |

## 🔍 Database

### H2 Console (Development)
- **URL**: http://localhost:4040/h2-console
- **JDBC URL**: `jdbc:h2:mem:customer_data`
- **Username**: `sa`
- **Password**: *(empty)*

### Entity Schema

```sql
CREATE TABLE customer (
    customer_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile_number BIGINT NOT NULL CHECK (mobile_number BETWEEN 1000000000 AND 9999999999),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## 🧪 Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomerServiceTest

# Run with coverage
mvn jacoco:prepare-agent test jacoco:report
```

### Test Categories
- **Unit Tests**: Service and repository layer tests
- **Integration Tests**: Full application context tests
- **Contract Tests**: API contract verification

## 📊 Monitoring

### Spring Boot Actuator Endpoints

```bash
# Health status
curl http://localhost:4040/actuator/health

# Application info
curl http://localhost:4040/actuator/info

# Metrics
curl http://localhost:4040/actuator/metrics

# Circuit breaker status
curl http://localhost:4040/actuator/circuitbreakers

# Environment properties
curl http://localhost:4040/actuator/env
```

### Prometheus Metrics

The application exposes Prometheus-compatible metrics at `/actuator/prometheus` including:
- JVM metrics (memory, GC, threads)
- HTTP request metrics
- Database connection pool metrics
- Resilience4j metrics (circuit breaker, retry, rate limiter)
- Custom business metrics

## 🏭 Production Deployment

### Kubernetes

```yaml
# Example Kubernetes deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: customer-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: customer-service
  template:
    metadata:
      labels:
        app: customer-service
    spec:
      containers:
      - name: customer-service
        image: customer-service:latest
        ports:
        - containerPort: 4040
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 4040
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 4040
          initialDelaySeconds: 30
          periodSeconds: 10
```

### Environment-Specific Configuration

#### Development
```properties
spring.profiles.active=default
spring.datasource.url=jdbc:h2:mem:customer_data
logging.level.com.kumar.mithlesh=DEBUG
```

#### Production
```properties
spring.profiles.active=kubernetes
spring.datasource.url=jdbc:postgresql://postgres:5432/customerdb
spring.cloud.config.uri=http://config-server:8888
logging.level.root=INFO
```

## 🐛 Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check logs
docker logs customer-service-app

# Common causes:
# - Port 4040 already in use
# - Insufficient memory
# - Configuration errors
```

#### Database Connection Issues
```bash
# Check H2 console access
curl http://localhost:4040/h2-console

# Verify database URL in logs
docker logs customer-service-app | grep -i datasource
```

#### Circuit Breaker Not Working
```bash
# Check circuit breaker status
curl http://localhost:4040/actuator/circuitbreakers

# View detailed health
curl http://localhost:4040/actuator/health
```

### Performance Tuning

#### JVM Options
```bash
# Container-optimized settings
-XX:+UseContainerSupport
-XX:MaxRAMPercentage=75.0
-XX:+UseG1GC
-XX:+UseStringDeduplication

# For debugging
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
```

#### Application Properties
```yaml
# Connection pool tuning
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA optimizations
spring.jpa.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
```

## 🔄 CI/CD Pipeline

### GitHub Actions Example

```yaml
name: CI/CD Pipeline
on:
  push:
    branches: [main, develop]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
      - name: Run tests
        run: mvn clean test

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build Docker image
        run: |
          docker build -t customer-service:${{ github.sha }} .
          docker tag customer-service:${{ github.sha }} customer-service:latest
```

## 📚 Technology Stack

### Core Technologies
- **Java 21** - Programming language
- **Spring Boot 3.4.0** - Application framework
- **Spring Data JPA** - Data access layer
- **H2 Database** - Development database
- **Maven** - Build tool

### Cloud & Resilience
- **Spring Cloud Config** - External configuration
- **Resilience4j** - Fault tolerance library
- **Spring Cloud LoadBalancer** - Client-side load balancing

### Containerization & Orchestration
- **Docker** - Containerization
- **Docker Compose** - Multi-container applications
- **Kubernetes** - Container orchestration (production)

### Monitoring & Observability
- **Spring Boot Actuator** - Production-ready features
- **Micrometer** - Metrics collection
- **Prometheus** - Metrics storage
- **Grafana** - Visualization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- Follow Spring Boot best practices
- Maintain test coverage above 80%
- Use conventional commit messages
- Document public APIs

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚡ Virtual Threads & Scheduling

### Scheduled Tasks

The application includes several scheduled tasks running on virtual threads:

- **Health Check**: Every 30 seconds - System health monitoring
- **Database Cleanup**: Daily at 2 AM - Maintenance operations
- **Data Sync**: Every 5 minutes - Customer data synchronization
- **Metrics Collection**: Every 2 minutes - Async metrics gathering
- **Service Monitor**: Every minute - External service connectivity
- **Bulk Processing**: Every 10 minutes - Heavy I/O operations
- **Statistics**: Every hour - System performance reporting

### Virtual Thread Benefits

- **High Throughput**: Handle thousands of concurrent I/O operations
- **Low Memory Footprint**: Virtual threads use minimal native memory
- **Simplified Programming**: Write blocking code that scales like async
- **Spring Integration**: Seamless integration with Spring's task execution

### Configuration

```yaml
server:
  tomcat:
    threads:
      max: 200  # Optimized for virtual threads
      min-spare: 10

spring:
  task:
    execution:
      pool:
        core-size: 8
        max-size: 20
    scheduling:
      pool:
        size: 10
```

## 🚀 Roadmap

- [x] **Virtual Threads**: Project Loom integration with Spring Boot
- [x] **Scheduling**: Background task processing with virtual threads
- [ ] **Security**: OAuth2/JWT authentication
- [ ] **Database**: PostgreSQL integration
- [ ] **Caching**: Redis integration
- [ ] **Messaging**: Kafka integration
- [ ] **Tracing**: Distributed tracing with Zipkin
- [ ] **API Documentation**: OpenAPI/Swagger integration

---

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section above

**Built with ❤️ using Spring Boot and modern cloud-native patterns**