# Customer Service - Modern Spring Boot Microservice

A modernized Spring Boot microservice demonstrating **Kubernetes-native deployment** with **Resilience4j patterns** and **Spring Cloud Config**.

## 🚀 Modern Architecture Features

### ✅ Kubernetes-Native Design
- **Service Discovery**: Uses Kubernetes DNS (`servicename.namespace.svc.cluster.local`)
- **Configuration**: External config via Spring Cloud Config Server
- **Health Checks**: Kubernetes liveness/readiness probes
- **Auto-scaling**: Horizontal Pod Autoscaler (CPU/Memory/Custom metrics)
- **Load Balancing**: Native Kubernetes service load balancing
- **Ingress**: NGINX Ingress controller with TLS termination

### ✅ Resilience4j Patterns
- **Circuit Breaker**: Multiple configurations (default, external-service, critical-service)
- **Retry**: Exponential backoff with jitter
- **Rate Limiter**: Request throttling per time window
- **Bulkhead**: Thread pool and semaphore isolation
- **Time Limiter**: Timeout protection for async calls

### ✅ Spring Cloud Modern Stack
- **Spring Boot**: 3.4.0 with Java 21
- **Spring Cloud Config**: Externalized configuration management
- **Spring Cloud LoadBalancer**: Client-side load balancing
- **Spring Cloud Circuit Breaker**: Resilience4j integration
- **Spring Boot Actuator**: Comprehensive monitoring endpoints

## 📁 Project Structure

```
customer-service/
├── src/main/java/com/kumar/mithlesh/
│   ├── CustomerServiceApplication.java      # Main application with @RefreshScope
│   ├── config/
│   │   ├── KubernetesResilienceConfig.java  # Resilience4j configuration
│   │   ├── RestTemplateConfig.java          # Load-balanced RestTemplate
│   │   └── CustomerServiceConfig.java       # Application configuration
│   ├── controller/
│   │   ├── CustomerController.java          # Core business endpoints
│   │   └── ResilienceController.java        # Resilience pattern demos
│   ├── service/
│   │   ├── CustomerService.java             # Core business service
│   │   └── KubernetesAwareService.java      # K8s and Resilience demos
│   └── model/
│       └── Customer.java                    # Domain model
├── k8s/                                     # Kubernetes manifests
│   ├── configmap.yaml                       # Configuration data
│   ├── secret.yaml                          # Sensitive data
│   ├── deployment.yaml                      # Pod deployment
│   ├── service.yaml                         # Service discovery
│   ├── ingress.yaml                         # External access
│   ├── hpa.yaml                            # Auto-scaling
│   └── monitoring.yaml                      # Prometheus integration
├── bootstrap.yml                           # Spring Cloud Config bootstrap
├── application.yml                         # Application configuration
└── Dockerfile                              # Multi-stage container build
```

## 🔧 Configuration Highlights

### Resilience4j Patterns Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      default:
        failure-rate-threshold: 50
        slow-call-rate-threshold: 50
        slow-call-duration-threshold: 2s
        permitted-number-of-calls-in-half-open-state: 3
        max-wait-duration-in-half-open-state: 5s
        sliding-window-size: 10
        minimum-number-of-calls: 5
        wait-duration-in-open-state: 10s
      
      external-service:
        failure-rate-threshold: 60
        slow-call-rate-threshold: 60
        sliding-window-size: 20
        minimum-number-of-calls: 10
        wait-duration-in-open-state: 30s
      
      critical-service:
        failure-rate-threshold: 30
        slow-call-rate-threshold: 40
        sliding-window-size: 15
        minimum-number-of-calls: 8
        wait-duration-in-open-state: 60s

  retry:
    instances:
      default:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
        enable-exponential-backoff: true
        enable-random-jitter: true
      
      kubernetes-service:
        max-attempts: 5
        wait-duration: 500ms
        exponential-backoff-multiplier: 1.5

  ratelimiter:
    instances:
      default:
        limit-for-period: 10
        limit-refresh-period: 1s
        timeout-duration: 0s
      
      api-requests:
        limit-for-period: 50
        limit-refresh-period: 1s
        timeout-duration: 100ms

  bulkhead:
    instances:
      default:
        max-concurrent-calls: 5
        max-wait-duration: 1s
      
      api-calls:
        max-concurrent-calls: 10
        max-wait-duration: 2s

  timelimiter:
    instances:
      default:
        timeout-duration: 3s
        cancel-running-future: true
```

### Spring Cloud Config Integration

```yaml
# bootstrap.yml
spring:
  application:
    name: customer-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  cloud:
    config:
      uri: ${CONFIG_SERVER_URI:http://config-server.default.svc.cluster.local:8888}
      fail-fast: true
      retry:
        initial-interval: 2000
        max-attempts: 6
        max-interval: 10000
        multiplier: 1.2
```

## 🚀 Kubernetes Deployment

### Quick Start

1. **Build and Deploy**:
   ```bash
   ./deploy.sh build-and-deploy
   ```

2. **Check Status**:
   ```bash
   kubectl get pods,svc,ingress -l app=customer-service
   ```

3. **View Logs**:
   ```bash
   kubectl logs -f deployment/customer-service
   ```

### Manual Deployment Steps

1. **Apply Kubernetes Manifests**:
   ```bash
   kubectl apply -f k8s/
   ```

2. **Verify Deployment**:
   ```bash
   kubectl rollout status deployment/customer-service
   ```

3. **Access Application**:
   ```bash
   # Via Ingress (if configured)
   curl https://customer-service.yourdomain.com/api/v1/health
   
   # Via Port-Forward
   kubectl port-forward service/customer-service 8080:8080
   curl http://localhost:8080/api/v1/health
   ```

## 🧪 Testing Resilience Patterns

### Circuit Breaker Testing

```bash
# Check circuit breaker status
curl http://localhost:8080/actuator/circuitbreakers

# Test external service call with circuit breaker
curl http://localhost:8080/api/v1/resilience/external/http%3A%2F%2Fexternal-api/health

# Test Kubernetes service call
curl http://localhost:8080/api/v1/resilience/k8s/user-service/users
```

### Rate Limiter Testing

```bash
# Test rate limiting (make multiple rapid requests)
for i in {1..20}; do curl http://localhost:8080/api/v1/resilience/rate-limit-test; done
```

### Retry Pattern Testing

```bash
# Test retry mechanism
curl http://localhost:8080/api/v1/resilience/retry-test
```

### Bulkhead Testing

```bash
# Test bulkhead isolation
curl http://localhost:8080/api/v1/resilience/bulkhead-test
```

## 📊 Monitoring and Observability

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Circuit breaker details
curl http://localhost:8080/actuator/circuitbreakers

# Rate limiter details
curl http://localhost:8080/actuator/ratelimiters

# Kubernetes information
curl http://localhost:8080/api/v1/resilience/k8s-info
```

### Prometheus Integration

The service exposes metrics compatible with Prometheus:

```bash
# Prometheus metrics endpoint
curl http://localhost:8080/actuator/prometheus
```

## 🔄 Service Discovery

### Kubernetes DNS

Services are discovered using Kubernetes DNS:
```
http://service-name.namespace.svc.cluster.local:port
```

Example:
```java
String kubernetesServiceUrl = String.format("http://%s.%s.svc.cluster.local", 
                                           serviceName, kubernetesNamespace);
```

### Service Discovery Endpoints

```bash
# Discover services
curl http://localhost:8080/api/v1/resilience/discover/user-service

# Check service health
curl http://localhost:8080/api/v1/resilience/health/user-service
```

## 🛡️ Security Features

- **Non-root container**: Runs with user ID 1000
- **Read-only root filesystem**: Enhanced security posture
- **Security context**: Defined security constraints
- **Resource limits**: CPU and memory constraints
- **Network policies**: Traffic isolation (when enabled)

## 🔧 Development

### Local Development

1. **Run Config Server** (if using Spring Cloud Config):
   ```bash
   # Run config server locally or use embedded configuration
   ```

2. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Run Tests**:
   ```bash
   mvn test
   ```

### Docker Development

```bash
# Build image
docker build -t customer-service:latest .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  customer-service:latest
```

## 📚 API Documentation

### Core Endpoints

- `GET /api/v1/health` - Health check
- `GET /api/v1/customers` - Get all customers
- `GET /api/v1/customers/{id}` - Get customer by ID
- `POST /api/v1/customers` - Create customer
- `PUT /api/v1/customers/{id}` - Update customer
- `DELETE /api/v1/customers/{id}` - Delete customer

### Resilience Pattern Endpoints

- `GET /api/v1/resilience/external/{serviceUrl}/{endpoint}` - External service call with circuit breaker
- `GET /api/v1/resilience/k8s/{serviceName}/{endpoint}` - Kubernetes service call
- `GET /api/v1/resilience/async/{serviceUrl}` - Async call with timeout
- `GET /api/v1/resilience/discover/{serviceName}` - Service discovery
- `GET /api/v1/resilience/health/{serviceName}` - Service health check
- `GET /api/v1/resilience/k8s-info` - Kubernetes pod information
- `GET /api/v1/resilience/circuit-breaker/status` - Circuit breaker status

## 🎯 Key Benefits

1. **Kubernetes-Native**: Designed for cloud-native environments
2. **Resilient**: Multiple fault-tolerance patterns
3. **Observable**: Comprehensive monitoring and metrics
4. **Scalable**: Auto-scaling based on metrics
5. **Secure**: Security best practices implemented
6. **Modern**: Latest Spring Boot and Java features
7. **Testable**: Comprehensive test coverage

## 🚧 Migration from Eureka

This service has been migrated from **Netflix Eureka** to **Kubernetes DNS** + **Spring Cloud Config**:

### Before (Eureka-based)
```java
@EnableEurekaClient
@EnableDiscoveryClient
```

### After (Kubernetes-native)
```java
@RefreshScope  // For dynamic config refresh
// Uses Kubernetes DNS for service discovery
// Spring Cloud Config for externalized configuration
```

### Benefits of Modern Approach
- **Simplified Architecture**: No need for separate Eureka server
- **Kubernetes Integration**: Native service discovery and load balancing
- **Better Resilience**: Circuit breakers, retry, and bulkhead patterns
- **Cloud-Native**: Designed for container orchestration platforms
- **Improved Performance**: Direct service-to-service communication

---

**Built with ❤️ using Spring Boot 3.4.0, Java 21, and Kubernetes**