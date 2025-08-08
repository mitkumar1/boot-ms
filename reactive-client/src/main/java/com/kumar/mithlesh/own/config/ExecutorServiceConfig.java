package com.kumar.mithlesh.own.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig {
    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}
