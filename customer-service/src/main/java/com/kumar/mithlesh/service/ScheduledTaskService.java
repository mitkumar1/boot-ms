package com.kumar.mithlesh.service;

import com.kumar.mithlesh.model.Customer;
import com.kumar.mithlesh.repo.CustomerRepoIface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled tasks service demonstrating Virtual Threads with Spring Scheduling
 * 
 * This service showcases:
 * - Cron-based scheduling
 * - Fixed rate scheduling
 * - Fixed delay scheduling
 * - Async scheduled tasks
 * - Virtual thread utilization for I/O operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {

    private final CustomerRepoIface customerRepository;
    private final KubernetesAwareService kubernetesAwareService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Health check task - runs every 30 seconds
     * Uses virtual threads for non-blocking execution
     */
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    public void performHealthCheck() {
        // This runs in a virtual thread automatically due to our configuration
        log.info("🏥 [HEALTH-CHECK] Performing system health check at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        try {
            // Simulate health check operations
            long customerCount = customerRepository.count();
            log.info("🏥 [HEALTH-CHECK] System healthy - {} customers in database", customerCount);
            
            // Log virtual thread information
            var currentThread = Thread.currentThread();
            log.info("🧵 [THREAD-INFO] Health check running on: {} (Virtual: {})", 
                currentThread.getName(), currentThread.isVirtual());
                
        } catch (Exception e) {
            log.error("❌ [HEALTH-CHECK] Health check failed", e);
        }
    }

    /**
     * Database cleanup task - runs daily at 2 AM
     * Demonstrates cron expression with virtual threads
     */
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void performDatabaseCleanup() {
        log.info("🧹 [CLEANUP] Starting database cleanup at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        try {
            // Example: Clean up old test data or perform maintenance
            var currentThread = Thread.currentThread();
            log.info("🧵 [THREAD-INFO] Cleanup running on: {} (Virtual: {})", 
                currentThread.getName(), currentThread.isVirtual());
            
            // Simulate cleanup operations
            Thread.sleep(2000); // This won't block the platform thread due to virtual threads
            
            log.info("✅ [CLEANUP] Database cleanup completed successfully");
            
        } catch (Exception e) {
            log.error("❌ [CLEANUP] Database cleanup failed", e);
        }
    }

    /**
     * Customer data sync task - runs every 5 minutes with initial delay
     * Demonstrates fixed delay scheduling
     */
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES, initialDelay = 1)
    public void syncCustomerData() {
        log.info("🔄 [SYNC] Starting customer data synchronization at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        try {
            List<Customer> customers = customerRepository.findAll();
            log.info("🔄 [SYNC] Processing {} customers for synchronization", customers.size());
            
            // Process each customer (this could be I/O intensive)
            customers.parallelStream().forEach(customer -> {
                try {
                    // Simulate external API call or data processing
                    Thread.sleep(100); // Virtual threads make this efficient
                    log.debug("🔄 [SYNC] Processed customer: {}", customer.getEmail());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("🔄 [SYNC] Customer sync interrupted for: {}", customer.getEmail());
                }
            });
            
            var currentThread = Thread.currentThread();
            log.info("🧵 [THREAD-INFO] Sync running on: {} (Virtual: {})", 
                currentThread.getName(), currentThread.isVirtual());
            
            log.info("✅ [SYNC] Customer data synchronization completed");
            
        } catch (Exception e) {
            log.error("❌ [SYNC] Customer data synchronization failed", e);
        }
    }

    /**
     * Async metrics collection task - runs every 2 minutes
     * Demonstrates async scheduling with CompletableFuture
     */
    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.MINUTES)
    public void collectMetrics() {
        log.info("📊 [METRICS] Starting metrics collection at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        CompletableFuture.runAsync(() -> {
            try {
                var currentThread = Thread.currentThread();
                log.info("🧵 [THREAD-INFO] Metrics collection running on: {} (Virtual: {})", 
                    currentThread.getName(), currentThread.isVirtual());
                
                // Collect various metrics
                long customerCount = customerRepository.count();
                
                // Simulate calling external monitoring service
                Thread.sleep(1000);
                
                log.info("📊 [METRICS] Collected metrics - Customers: {}", customerCount);
                log.info("✅ [METRICS] Metrics collection completed");
                
            } catch (Exception e) {
                log.error("❌ [METRICS] Metrics collection failed", e);
            }
        });
    }

    /**
     * Service health monitoring - runs every minute
     * Tests external service connectivity using virtual threads
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES, initialDelay = 30)
    public void monitorExternalServices() {
        log.info("🌐 [SERVICE-MONITOR] Checking external services at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        try {
            var currentThread = Thread.currentThread();
            log.info("🧵 [THREAD-INFO] Service monitor running on: {} (Virtual: {})", 
                currentThread.getName(), currentThread.isVirtual());
            
            // Test external service connectivity (this could involve HTTP calls)
            // Virtual threads make this very efficient for I/O bound operations
            checkExternalServiceConnectivity();
            
            log.info("✅ [SERVICE-MONITOR] External service monitoring completed");
            
        } catch (Exception e) {
            log.error("❌ [SERVICE-MONITOR] External service monitoring failed", e);
        }
    }

    /**
     * Bulk data processing task - runs every 10 minutes
     * Demonstrates heavy I/O operations with virtual threads
     */
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void processBulkData() {
        log.info("⚡ [BULK-PROCESS] Starting bulk data processing at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        CompletableFuture.runAsync(() -> {
            try {
                var currentThread = Thread.currentThread();
                log.info("🧵 [THREAD-INFO] Bulk processing running on: {} (Virtual: {})", 
                    currentThread.getName(), currentThread.isVirtual());
                
                // Simulate bulk processing operations
                List<Customer> customers = customerRepository.findAll();
                
                // Process in parallel using virtual threads
                customers.parallelStream().forEach(customer -> {
                    try {
                        // Simulate complex processing with I/O operations
                        Thread.sleep(200); // Virtual threads handle this efficiently
                        
                        // Example: Update customer statistics, call external APIs, etc.
                        log.debug("⚡ [BULK-PROCESS] Processed customer ID: {}", customer.getCustomerId());
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("⚡ [BULK-PROCESS] Processing interrupted for customer: {}", 
                            customer.getCustomerId());
                    }
                });
                
                log.info("✅ [BULK-PROCESS] Bulk data processing completed for {} customers", 
                    customers.size());
                
            } catch (Exception e) {
                log.error("❌ [BULK-PROCESS] Bulk data processing failed", e);
            }
        });
    }

    /**
     * System statistics reporting - runs every hour
     * Demonstrates comprehensive system monitoring
     */
    @Scheduled(cron = "0 0 * * * ?") // Every hour
    public void reportSystemStatistics() {
        log.info("📈 [STATS] Generating system statistics report at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        try {
            var currentThread = Thread.currentThread();
            log.info("🧵 [THREAD-INFO] Statistics reporting running on: {} (Virtual: {})", 
                currentThread.getName(), currentThread.isVirtual());
            
            // Collect system statistics
            long totalCustomers = customerRepository.count();
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            log.info("📈 [STATS] System Statistics Report:");
            log.info("📈 [STATS] - Total Customers: {}", totalCustomers);
            log.info("📈 [STATS] - Memory Usage: {} MB / {} MB", 
                usedMemory / 1024 / 1024, totalMemory / 1024 / 1024);
            log.info("📈 [STATS] - Virtual Thread: {}", currentThread.isVirtual());
            
            log.info("✅ [STATS] System statistics report completed");
            
        } catch (Exception e) {
            log.error("❌ [STATS] System statistics reporting failed", e);
        }
    }

    /**
     * Helper method to check external service connectivity
     */
    private void checkExternalServiceConnectivity() {
        try {
            // Example: Check if we can reach an external service
            // This simulates HTTP calls or database connectivity checks
            
            // Using our Kubernetes-aware service to test connectivity
            // This will use circuit breakers and other resilience patterns
            String result = kubernetesAwareService.callKubernetesService("health-check", "ping");
            log.debug("🌐 [SERVICE-MONITOR] External service response: {}", result);
            
        } catch (Exception e) {
            log.warn("🌐 [SERVICE-MONITOR] External service connectivity issue: {}", e.getMessage());
        }
    }
}