package com.kumar.mithlesh.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;

/**
 * Configuration for Virtual Threads and Task Scheduling
 * 
 * This configuration enables:
 * - Virtual Threads for web requests (Tomcat)
 * - Virtual Threads for async tasks
 * - Scheduled tasks with virtual thread support
 * - Custom task executors optimized for virtual threads
 */
@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
public class VirtualThreadConfig {

    /**
     * Configure Tomcat to use Virtual Threads for handling HTTP requests
     * This dramatically improves throughput for I/O-bound operations
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        log.info("Configuring Tomcat to use Virtual Threads for HTTP requests");
        return protocolHandler -> protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Override the default async task executor to use Virtual Threads
     * This replaces the default ThreadPoolTaskExecutor with virtual threads
     */
    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
        log.info("Creating Virtual Thread-based AsyncTaskExecutor");
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Task scheduler with Virtual Thread support for @Scheduled methods
     * This provides better scalability for scheduled tasks
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // Small pool as virtual threads are lightweight
        scheduler.setThreadNamePrefix("scheduled-vt-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        
        // Configure to use virtual threads
        scheduler.setThreadFactory(Thread.ofVirtual()
            .name("scheduled-task-", 0)
            .factory());
            
        log.info("TaskScheduler configured with Virtual Threads support");
        return scheduler;
    }

    /**
     * Custom executor for background processing tasks
     * Useful for fire-and-forget operations
     */
    @Bean("backgroundTaskExecutor")
    public AsyncTaskExecutor backgroundTaskExecutor() {
        log.info("Creating background task executor with Virtual Threads");
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * Bulk processing executor for handling large datasets
     * Optimized for CPU-intensive + I/O operations
     */
    @Bean("bulkProcessingExecutor")
    public AsyncTaskExecutor bulkProcessingExecutor() {
        log.info("Creating bulk processing executor with Virtual Threads");
        return new TaskExecutorAdapter(
            Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                    .name("bulk-processor-", 0)
                    .factory()
            )
        );
    }
}