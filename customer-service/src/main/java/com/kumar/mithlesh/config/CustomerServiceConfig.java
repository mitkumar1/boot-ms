package com.kumar.mithlesh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * Configuration properties that can be refreshed from Spring Cloud Config
 * without restarting the application.
 * 
 * Modern approach using @ConfigurationProperties with @RefreshScope
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "customer-service")
public class CustomerServiceConfig {

    /**
     * Maximum number of customers that can be retrieved in a single request
     */
    private int maxCustomersPerRequest = 100;

    /**
     * Enable/disable advanced features
     */
    private boolean advancedFeaturesEnabled = true;

    /**
     * Welcome message that can be configured externally
     */
    private String welcomeMessage = "Welcome to Customer Service API";

    /**
     * API version information
     */
    private String version = "1.0.0";

    /**
     * External service settings
     */
    private ExternalServices externalServices = new ExternalServices();

    @Data
    public static class ExternalServices {
        private String notificationServiceUrl = "http://notification-service:8080";
        private String emailServiceUrl = "http://email-service:8081";
        private int timeoutInSeconds = 30;
        private boolean retryEnabled = true;
        private int maxRetries = 3;
    }
}