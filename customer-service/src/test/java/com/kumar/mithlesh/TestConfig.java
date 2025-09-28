package com.kumar.mithlesh;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public RestTemplate loadBalancedRestTemplate() {
        return mock(RestTemplate.class);
    }
    
    @Bean("externalRestTemplate")
    public RestTemplate externalRestTemplate() {
        return mock(RestTemplate.class);
    }
}