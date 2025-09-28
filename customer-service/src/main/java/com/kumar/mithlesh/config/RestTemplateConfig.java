package com.kumar.mithlesh.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate with load balancing support
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Load-balanced RestTemplate for service-to-service calls
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Regular RestTemplate for external calls (non-load-balanced)
     */
    @Bean("externalRestTemplate")
    public RestTemplate externalRestTemplate() {
        return new RestTemplate();
    }
}