package com.kumar.mithlesh.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Kubernetes-aware Resilience4j configuration
 * Provides circuit breaker patterns optimized for microservices in Kubernetes
 */
@Configuration
public class KubernetesResilienceConfig {

    /**
     * Default circuit breaker configuration for Kubernetes services
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(15))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(20)
                        .minimumNumberOfCalls(10)
                        .slowCallRateThreshold(50.0f)
                        .slowCallDurationThreshold(Duration.ofSeconds(3))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .maxWaitDurationInHalfOpenState(Duration.ofSeconds(10))
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(20))
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .build());
    }

    /**
     * Circuit breaker configuration for external services (outside Kubernetes cluster)
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> externalServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(30))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(15)
                        .minimumNumberOfCalls(8)
                        .slowCallRateThreshold(40.0f)
                        .slowCallDurationThreshold(Duration.ofSeconds(5))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .failureRateThreshold(60.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build()), "external-service");
    }

    /**
     * Fast-fail circuit breaker for critical Kubernetes services
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> criticalServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .slowCallRateThreshold(30.0f)
                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                        .permittedNumberOfCallsInHalfOpenState(2)
                        .failureRateThreshold(30.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build()), "critical-service");
    }
}