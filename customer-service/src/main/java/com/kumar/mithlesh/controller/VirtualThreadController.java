package com.kumar.mithlesh.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller demonstrating Virtual Threads capabilities and monitoring
 * 
 * This controller provides endpoints to:
 * - Monitor virtual thread usage
 * - Test async operations with virtual threads
 * - Demonstrate thread performance comparisons
 * - Provide thread metrics and statistics
 */
@RestController
@RequestMapping("/api/v1/virtual-threads")
@Slf4j
@RequiredArgsConstructor
public class VirtualThreadController {

    private final AsyncTaskExecutor backgroundTaskExecutor;
    private final AsyncTaskExecutor bulkProcessingExecutor;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Get current thread information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getThreadInfo() {
        Thread currentThread = Thread.currentThread();
        
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put("threadName", currentThread.getName());
        threadInfo.put("isVirtual", currentThread.isVirtual());
        threadInfo.put("threadId", currentThread.threadId());
        threadInfo.put("timestamp", LocalDateTime.now().format(FORMATTER));
        threadInfo.put("state", currentThread.getState().toString());
        
        log.info("🧵 [THREAD-INFO] Request handled by: {} (Virtual: {})", 
            currentThread.getName(), currentThread.isVirtual());
        
        return ResponseEntity.ok(threadInfo);
    }

    /**
     * Test I/O intensive operation with virtual threads
     */
    @GetMapping("/test-io/{delayMs}")
    public ResponseEntity<Map<String, Object>> testIOOperation(@PathVariable int delayMs) {
        long startTime = System.currentTimeMillis();
        Thread currentThread = Thread.currentThread();
        
        log.info("⚡ [IO-TEST] Starting I/O operation with {}ms delay on thread: {} (Virtual: {})", 
            delayMs, currentThread.getName(), currentThread.isVirtual());
        
        try {
            // Simulate I/O operation - virtual threads handle this efficiently
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("⚡ [IO-TEST] Operation interrupted");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("requestedDelay", delayMs);
        result.put("actualDuration", duration);
        result.put("threadName", currentThread.getName());
        result.put("isVirtual", currentThread.isVirtual());
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        log.info("✅ [IO-TEST] I/O operation completed in {}ms", duration);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Async operation using virtual threads
     */
    @Async
    @PostMapping("/async-task")
    public CompletableFuture<Map<String, Object>> executeAsyncTask(@RequestBody Map<String, Object> payload) {
        return CompletableFuture.supplyAsync(() -> {
            Thread currentThread = Thread.currentThread();
            long startTime = System.currentTimeMillis();
            
            log.info("🚀 [ASYNC-TASK] Starting async task on thread: {} (Virtual: {})", 
                currentThread.getName(), currentThread.isVirtual());
            
            try {
                // Simulate some processing
                int processingTime = (Integer) payload.getOrDefault("processingTimeMs", 1000);
                Thread.sleep(processingTime);
                
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Async task completed successfully");
                result.put("payload", payload);
                result.put("threadName", currentThread.getName());
                result.put("isVirtual", currentThread.isVirtual());
                result.put("processingTime", System.currentTimeMillis() - startTime);
                result.put("timestamp", LocalDateTime.now().format(FORMATTER));
                
                log.info("✅ [ASYNC-TASK] Async task completed in {}ms", 
                    System.currentTimeMillis() - startTime);
                
                return result;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("🚀 [ASYNC-TASK] Async task interrupted");
                
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", "Task was interrupted");
                errorResult.put("threadName", currentThread.getName());
                errorResult.put("isVirtual", currentThread.isVirtual());
                return errorResult;
            }
        });
    }

    /**
     * Bulk operation test with virtual threads
     */
    @PostMapping("/bulk-test/{numberOfTasks}")
    public ResponseEntity<Map<String, Object>> testBulkOperations(@PathVariable int numberOfTasks) {
        long startTime = System.currentTimeMillis();
        Thread currentThread = Thread.currentThread();
        
        log.info("⚡ [BULK-TEST] Starting bulk test with {} tasks on thread: {} (Virtual: {})", 
            numberOfTasks, currentThread.getName(), currentThread.isVirtual());
        
        // Execute multiple async tasks
        CompletableFuture<?>[] futures = new CompletableFuture[numberOfTasks];
        
        for (int i = 0; i < numberOfTasks; i++) {
            final int taskId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                Thread taskThread = Thread.currentThread();
                try {
                    // Simulate I/O operation
                    Thread.sleep(100 + (taskId % 500)); // Variable delay
                    log.debug("⚡ [BULK-TEST] Task {} completed on thread: {} (Virtual: {})", 
                        taskId, taskThread.getName(), taskThread.isVirtual());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("⚡ [BULK-TEST] Task {} interrupted", taskId);
                }
            }, bulkProcessingExecutor);
        }
        
        // Wait for all tasks to complete
        CompletableFuture.allOf(futures).join();
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("numberOfTasks", numberOfTasks);
        result.put("totalDuration", totalDuration);
        result.put("averageTimePerTask", (double) totalDuration / numberOfTasks);
        result.put("tasksPerSecond", (double) numberOfTasks / (totalDuration / 1000.0));
        result.put("coordinatorThread", currentThread.getName());
        result.put("isVirtualCoordinator", currentThread.isVirtual());
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        log.info("✅ [BULK-TEST] Bulk test completed: {} tasks in {}ms ({} tasks/sec)", 
            numberOfTasks, totalDuration, String.format("%.2f", (double) numberOfTasks / (totalDuration / 1000.0)));
        
        return ResponseEntity.ok(result);
    }

    /**
     * Performance comparison between operations
     */
    @GetMapping("/performance-test/{iterations}")
    public ResponseEntity<Map<String, Object>> performanceTest(@PathVariable int iterations) {
        Thread currentThread = Thread.currentThread();
        
        log.info("🏁 [PERF-TEST] Starting performance test with {} iterations on thread: {} (Virtual: {})", 
            iterations, currentThread.getName(), currentThread.isVirtual());
        
        long startTime = System.currentTimeMillis();
        
        // Simulate CPU + I/O intensive work
        for (int i = 0; i < iterations; i++) {
            try {
                // Mix of CPU work and I/O simulation
                double result = Math.pow(i, 2) + Math.sqrt(i);
                
                // Simulate I/O operation every 10th iteration
                if (i % 10 == 0) {
                    Thread.sleep(10); // Virtual threads make this efficient
                }
                
                if (i % 100 == 0) {
                    log.debug("🏁 [PERF-TEST] Completed {} iterations", i);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("🏁 [PERF-TEST] Performance test interrupted at iteration {}", i);
                break;
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("iterations", iterations);
        result.put("duration", duration);
        result.put("iterationsPerSecond", (double) iterations / (duration / 1000.0));
        result.put("threadName", currentThread.getName());
        result.put("isVirtual", currentThread.isVirtual());
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        log.info("✅ [PERF-TEST] Performance test completed: {} iterations in {}ms ({} iter/sec)", 
            iterations, duration, String.format("%.2f", (double) iterations / (duration / 1000.0)));
        
        return ResponseEntity.ok(result);
    }

    /**
     * Get JVM and thread statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getThreadStats() {
        Thread currentThread = Thread.currentThread();
        Runtime runtime = Runtime.getRuntime();
        
        Map<String, Object> stats = new HashMap<>();
        
        // JVM Statistics
        stats.put("totalMemory", runtime.totalMemory());
        stats.put("freeMemory", runtime.freeMemory());
        stats.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        stats.put("maxMemory", runtime.maxMemory());
        stats.put("availableProcessors", runtime.availableProcessors());
        
        // Thread Statistics
        stats.put("currentThreadName", currentThread.getName());
        stats.put("currentThreadVirtual", currentThread.isVirtual());
        stats.put("activeThreadCount", Thread.activeCount());
        
        // System Properties related to virtual threads
        stats.put("javaVersion", System.getProperty("java.version"));
        stats.put("javaVendor", System.getProperty("java.vendor"));
        stats.put("osName", System.getProperty("os.name"));
        stats.put("osVersion", System.getProperty("os.version"));
        
        stats.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        log.info("📊 [STATS] Thread statistics requested by thread: {} (Virtual: {})", 
            currentThread.getName(), currentThread.isVirtual());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Test concurrent requests handling
     */
    @GetMapping("/concurrent-test/{concurrency}")
    public ResponseEntity<Map<String, Object>> testConcurrency(@PathVariable int concurrency) {
        Thread coordinatorThread = Thread.currentThread();
        long startTime = System.currentTimeMillis();
        
        log.info("🔄 [CONCURRENT-TEST] Starting concurrency test with {} concurrent tasks on thread: {} (Virtual: {})", 
            concurrency, coordinatorThread.getName(), coordinatorThread.isVirtual());
        
        CompletableFuture<?>[] futures = new CompletableFuture[concurrency];
        
        for (int i = 0; i < concurrency; i++) {
            final int taskId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                Thread taskThread = Thread.currentThread();
                try {
                    // Simulate varying workloads
                    Thread.sleep(100 + (taskId % 300));
                    
                    // Some CPU work
                    double result = 0;
                    for (int j = 0; j < 1000; j++) {
                        result += Math.sin(j) * Math.cos(j);
                    }
                    
                    log.debug("🔄 [CONCURRENT-TEST] Task {} completed on thread: {} (Virtual: {})", 
                        taskId, taskThread.getName(), taskThread.isVirtual());
                        
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("🔄 [CONCURRENT-TEST] Task {} interrupted", taskId);
                }
            }, backgroundTaskExecutor);
        }
        
        // Wait for all tasks
        CompletableFuture.allOf(futures).join();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("concurrencyLevel", concurrency);
        result.put("totalDuration", duration);
        result.put("throughput", (double) concurrency / (duration / 1000.0));
        result.put("coordinatorThread", coordinatorThread.getName());
        result.put("isVirtualCoordinator", coordinatorThread.isVirtual());
        result.put("timestamp", LocalDateTime.now().format(FORMATTER));
        
        log.info("✅ [CONCURRENT-TEST] Concurrency test completed: {} tasks in {}ms (throughput: {} tasks/sec)", 
            concurrency, duration, String.format("%.2f", (double) concurrency / (duration / 1000.0)));
        
        return ResponseEntity.ok(result);
    }
}