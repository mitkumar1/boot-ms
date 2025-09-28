package com.kumar.mithlesh.controller;

import com.kumar.mithlesh.config.CustomerServiceConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Configuration and Health Controller demonstrating modern Spring Cloud features
 */
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final CustomerServiceConfig config;

    /**
     * Get current configuration (refreshable from Spring Cloud Config)
     */
    @GetMapping("/current")
    public ResponseEntity<CustomerServiceConfig> getCurrentConfig() {
        log.info("Fetching current configuration");
        return ResponseEntity.ok(config);
    }

    /**
     * Get welcome message with circuit breaker pattern
     */
    @GetMapping("/welcome")
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackWelcome")
    @RateLimiter(name = "default")
    @Retry(name = "default")
    public ResponseEntity<Map<String, String>> getWelcomeMessage() {
        log.info("Getting welcome message with circuit breaker");
        
        // Simulate random failures for circuit breaker demonstration
        if (ThreadLocalRandom.current().nextDouble() < 0.3) {
            throw new RuntimeException("Simulated service failure");
        }
        
        return ResponseEntity.ok(Map.of(
            "message", config.getWelcomeMessage(),
            "version", config.getVersion(),
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    /**
     * Fallback method for circuit breaker
     */
    public ResponseEntity<Map<String, String>> fallbackWelcome(Exception ex) {
        log.warn("Circuit breaker activated, returning fallback response", ex);
        return ResponseEntity.ok(Map.of(
            "message", "Service temporarily unavailable - Fallback Response",
            "version", "fallback",
            "timestamp", java.time.Instant.now().toString(),
            "error", "Circuit breaker activated"
        ));
    }

    /**
     * Get external services configuration
     */
    @GetMapping("/external-services")
    public ResponseEntity<CustomerServiceConfig.ExternalServices> getExternalServicesConfig() {
        log.info("Fetching external services configuration");
        return ResponseEntity.ok(config.getExternalServices());
    }

    /**
     * Health check with configuration info
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthWithConfig() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "customer-service",
            "version", config.getVersion(),
            "maxCustomersPerRequest", config.getMaxCustomersPerRequest(),
            "advancedFeaturesEnabled", config.isAdvancedFeaturesEnabled(),
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    /**
     * Test endpoint to trigger configuration refresh manually
     * In real applications, this would be triggered by webhooks from config server
     */
    @PostMapping("/test-config-change")
    public ResponseEntity<String> testConfigChange() {
        log.info("Config change test endpoint called - check /actuator/refresh for manual refresh");
        return ResponseEntity.ok(
            "Configuration test triggered. Use POST /actuator/refresh to reload config from Spring Cloud Config Server"
        );
    }
}