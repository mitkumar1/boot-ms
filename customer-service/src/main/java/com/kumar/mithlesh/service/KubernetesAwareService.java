package com.kumar.mithlesh.service;

import com.kumar.mithlesh.exception.ServiceCallException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Kubernetes-aware service demonstrating Resilience4j patterns
 * and Kubernetes DNS service discovery
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KubernetesAwareService {

    private final RestTemplate loadBalancedRestTemplate;
    private final DiscoveryClient discoveryClient;

    @Value("${spring.application.name:customer-service}")
    private String applicationName;

    @Value("${kubernetes.namespace:default}")
    private String kubernetesNamespace;

    /**
     * Call external service with circuit breaker, retry, and rate limiting
     */
    @CircuitBreaker(name = "external-service", fallbackMethod = "fallbackExternalService")
    @Retry(name = "kubernetes-service")
    @RateLimiter(name = "api-requests")
    @Bulkhead(name = "api-calls")
    public String callExternalService(String serviceUrl, String endpoint) {
        log.info("Calling external service: {}/{}", serviceUrl, endpoint);
        
        try {
            String response = loadBalancedRestTemplate.getForObject(serviceUrl + "/" + endpoint, String.class);
            log.info("External service call successful");
            return response;
        } catch (Exception e) {
            log.error("External service call failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Call Kubernetes service using DNS discovery
     */
    @CircuitBreaker(name = "kubernetes-service", fallbackMethod = "fallbackKubernetesService")
    @Retry(name = "kubernetes-service")
    @RateLimiter(name = "default")
    public String callKubernetesService(String serviceName, String endpoint) {
        // Use Kubernetes DNS for service discovery
        String kubernetesServiceUrl = String.format("http://%s.%s.svc.cluster.local", 
                                                   serviceName, kubernetesNamespace);
        
        log.info("Calling Kubernetes service: {}/{}", kubernetesServiceUrl, endpoint);
        
        try {
            String response = loadBalancedRestTemplate.getForObject(kubernetesServiceUrl + "/" + endpoint, String.class);
            log.info("Kubernetes service call successful");
            return response;
        } catch (Exception e) {
            log.error("Kubernetes service call failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Async call with time limiter
     */
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackAsyncCall")
    @TimeLimiter(name = "default")
    @Bulkhead(name = "default", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> asyncCallWithTimeout(String serviceUrl) {
        log.info("Making async call to: {}", serviceUrl);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate some processing time
                Thread.sleep(1000);
                return loadBalancedRestTemplate.getForObject(serviceUrl + "/health", String.class);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceCallException("Async call interrupted", e);
            } catch (Exception e) {
                log.error("Async call failed: {}", e.getMessage());
                throw new ServiceCallException("Async call failed", e);
            }
        });
    }

    /**
     * Discover services using Spring Cloud Discovery Client
     */
    public List<ServiceInstance> discoverServices(String serviceName) {
        log.info("Discovering services for: {}", serviceName);
        
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            log.info("Found {} instances for service: {}", instances.size(), serviceName);
            
            instances.forEach(instance -> 
                log.debug("Instance: {}:{}, metadata: {}", 
                         instance.getHost(), instance.getPort(), instance.getMetadata())
            );
            
            return instances;
        } catch (Exception e) {
            log.error("Service discovery failed for {}: {}", serviceName, e.getMessage());
            throw e;
        }
    }

    /**
     * Health check for dependent services
     */
    @CircuitBreaker(name = "critical-service", fallbackMethod = "fallbackHealthCheck")
    @Retry(name = "default")
    public boolean checkServiceHealth(String serviceName) {
        log.info("Checking health for service: {}", serviceName);
        
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            
            if (instances.isEmpty()) {
                log.warn("No instances found for service: {}", serviceName);
                return false;
            }
            
            // Check health of first available instance
            ServiceInstance instance = instances.get(0);
            String healthUrl = String.format("http://%s:%d/actuator/health", 
                                            instance.getHost(), instance.getPort());
            
            String healthStatus = loadBalancedRestTemplate.getForObject(healthUrl, String.class);
            boolean isHealthy = healthStatus != null && healthStatus.contains("\"status\":\"UP\"");
            
            log.info("Service {} health check result: {}", serviceName, isHealthy);
            return isHealthy;
            
        } catch (Exception e) {
            log.error("Health check failed for service {}: {}", serviceName, e.getMessage());
            return false;
        }
    }

    // Fallback methods

    public String fallbackExternalService(String serviceUrl, String endpoint, Exception ex) {
        log.warn("External service fallback triggered for {}/{}: {}", serviceUrl, endpoint, ex.getMessage());
        return String.format("{\"status\":\"fallback\",\"message\":\"External service %s temporarily unavailable\",\"timestamp\":\"%s\"}", 
                           serviceUrl, java.time.Instant.now());
    }

    public String fallbackKubernetesService(String serviceName, String endpoint, Exception ex) {
        log.warn("Kubernetes service fallback triggered for {}/{}: {}", serviceName, endpoint, ex.getMessage());
        return String.format("{\"status\":\"fallback\",\"message\":\"Kubernetes service %s temporarily unavailable\",\"timestamp\":\"%s\"}", 
                           serviceName, java.time.Instant.now());
    }

    public CompletableFuture<String> fallbackAsyncCall(String serviceUrl, Exception ex) {
        log.warn("Async call fallback triggered for {}: {}", serviceUrl, ex.getMessage());
        return CompletableFuture.completedFuture(
            String.format("{\"status\":\"fallback\",\"message\":\"Async service temporarily unavailable\",\"timestamp\":\"%s\"}", 
                         java.time.Instant.now())
        );
    }

    public boolean fallbackHealthCheck(String serviceName, Exception ex) {
        log.warn("Health check fallback triggered for {}: {}", serviceName, ex.getMessage());
        // In fallback, assume service is unhealthy
        return false;
    }

    /**
     * Get current application's Kubernetes information
     */
    public String getKubernetesInfo() {
        return String.format(
            "{\"application\":\"%s\",\"namespace\":\"%s\",\"podName\":\"%s\",\"podIP\":\"%s\",\"nodeName\":\"%s\"}", 
            applicationName,
            kubernetesNamespace,
            System.getenv("POD_NAME"),
            System.getenv("POD_IP"),
            System.getenv("NODE_NAME")
        );
    }
}