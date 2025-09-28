package com.kumar.mithlesh.controller;

import com.kumar.mithlesh.service.KubernetesAwareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enhanced controller demonstrating Kubernetes and Resilience4j integration
 */
@RestController
@RequestMapping("/api/v1/resilience")
@Slf4j
@RequiredArgsConstructor
public class ResilienceController {
    
    private static final String CIRCUIT_BREAKER_INFO = "Available - check actuator/circuitbreakers";
    
    private final KubernetesAwareService kubernetesAwareService;
    
    /**
     * Call external service with circuit breaker protection
     */
    @GetMapping("/external/{serviceUrl}/{endpoint}")
    public ResponseEntity<String> callExternalService(
            @PathVariable String serviceUrl, 
            @PathVariable String endpoint) {
        
        log.info("Controller: Calling external service {}/{}", serviceUrl, endpoint);
        
        try {
            String result = kubernetesAwareService.callExternalService(serviceUrl, endpoint);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("External service call failed: {}", e.getMessage());
            return ResponseEntity.status(503).body("Service temporarily unavailable");
        }
    }
    
    /**
     * Call Kubernetes service using DNS discovery
     */
    @GetMapping("/k8s/{serviceName}/{endpoint}")
    public ResponseEntity<String> callKubernetesService(
            @PathVariable String serviceName, 
            @PathVariable String endpoint) {
        
        log.info("Controller: Calling Kubernetes service {}/{}", serviceName, endpoint);
        
        try {
            String result = kubernetesAwareService.callKubernetesService(serviceName, endpoint);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Kubernetes service call failed: {}", e.getMessage());
            return ResponseEntity.status(503).body("Kubernetes service temporarily unavailable");
        }
    }
    
    /**
     * Async call with timeout protection
     */
    @GetMapping("/async/{serviceUrl}")
    public CompletableFuture<ResponseEntity<String>> asyncCall(@PathVariable String serviceUrl) {
        log.info("Controller: Making async call to {}", serviceUrl);
        
        return kubernetesAwareService.asyncCallWithTimeout(serviceUrl)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    log.error("Async call failed: {}", ex.getMessage());
                    return ResponseEntity.status(503).body("Async service temporarily unavailable");
                });
    }
    
    /**
     * Discover services
     */
    @GetMapping("/discover/{serviceName}")
    public ResponseEntity<List<ServiceInstance>> discoverServices(@PathVariable String serviceName) {
        log.info("Controller: Discovering services for {}", serviceName);
        
        try {
            List<ServiceInstance> instances = kubernetesAwareService.discoverServices(serviceName);
            return ResponseEntity.ok(instances);
        } catch (Exception e) {
            log.error("Service discovery failed: {}", e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health/{serviceName}")
    public ResponseEntity<Map<String, Object>> checkServiceHealth(@PathVariable String serviceName) {
        log.info("Controller: Checking health for service {}", serviceName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("serviceName", serviceName);
        response.put("timestamp", java.time.Instant.now());
        
        try {
            boolean isHealthy = kubernetesAwareService.checkServiceHealth(serviceName);
            response.put("status", isHealthy ? "UP" : "DOWN");
            response.put("healthy", isHealthy);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            response.put("status", "ERROR");
            response.put("healthy", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(503).body(response);
        }
    }
    
    /**
     * Get Kubernetes information about current pod
     */
    @GetMapping("/k8s-info")
    public ResponseEntity<String> getKubernetesInfo() {
        log.info("Controller: Getting Kubernetes information");
        
        try {
            String kubernetesInfo = kubernetesAwareService.getKubernetesInfo();
            return ResponseEntity.ok(kubernetesInfo);
        } catch (Exception e) {
            log.error("Failed to get Kubernetes info: {}", e.getMessage());
            return ResponseEntity.status(500).body("Failed to get Kubernetes information");
        }
    }
    
    /**
     * Test circuit breaker status endpoint
     */
    @GetMapping("/circuit-breaker/status")
    public ResponseEntity<Map<String, String>> getCircuitBreakerStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("external-service", CIRCUIT_BREAKER_INFO);
        status.put("kubernetes-service", CIRCUIT_BREAKER_INFO);
        status.put("critical-service", CIRCUIT_BREAKER_INFO);
        status.put("default", CIRCUIT_BREAKER_INFO);
        status.put("info", "Visit /actuator/circuitbreakers for detailed status");
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Test rate limiter
     */
    @GetMapping("/rate-limit-test")
    public ResponseEntity<String> testRateLimit() {
        log.info("Testing rate limiter");
        return ResponseEntity.ok("Rate limiter test - timestamp: " + java.time.Instant.now());
    }
    
    /**
     * Test bulkhead pattern
     */
    @GetMapping("/bulkhead-test")
    public ResponseEntity<String> testBulkhead() {
        log.info("Testing bulkhead pattern");
        return ResponseEntity.ok("Bulkhead test - timestamp: " + java.time.Instant.now());
    }
    
    /**
     * Test retry mechanism
     */
    @GetMapping("/retry-test")
    public ResponseEntity<String> testRetry() {
        log.info("Testing retry mechanism");
        return ResponseEntity.ok("Retry test - timestamp: " + java.time.Instant.now());
    }
}