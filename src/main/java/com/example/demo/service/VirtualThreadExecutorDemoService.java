package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Service: Demonstrates why Executors.newVirtualThreadPerTaskExecutor() returns ExecutorService
 * 
 * This service demonstrates the key differences between Executor and ExecutorService
 * when working with virtual threads, designed to be called via REST endpoints.
 */
@Service
public class VirtualThreadExecutorDemoService {
    
    private static final Logger logger = LoggerFactory.getLogger(VirtualThreadExecutorDemoService.class);
    
    /**
     * Run the complete virtual thread executor demonstration
     * Returns structured results for API response
     */
    public ExecutorDemoResult runDemo() {
        logger.info("Starting Virtual Thread Executor Demo");
        
        ExecutorDemoResult result = new ExecutorDemoResult();
        
        try {
            // Create virtual thread executor service
            ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
            result.setExecutorCreated(true);
            
            // ====== EXECUTOR INTERFACE LIMITATIONS ======
            List<String> executorLimitations = new ArrayList<>();
            executorLimitations.add("Only has execute(Runnable) method");
            executorLimitations.add("No return values, no lifecycle management");
            result.setExecutorLimitations(executorLimitations);
            
            // What you can do with just Executor interface:
            Executor basicExecutor = executorService; // Widened reference
            basicExecutor.execute(() -> {
                logger.debug("Task executed via Executor interface");
            });
            
            // What you CANNOT do with just Executor interface:
            List<String> executorLimitationsDetailed = Arrays.asList(
                "Cannot get Future results",
                "Cannot manage lifecycle (shutdown)",
                "Cannot submit Callable tasks"
            );
            result.setExecutorLimitationsDetailed(executorLimitationsDetailed);
            
            // ====== EXECUTORSERVICE CAPABILITIES ======
            List<String> executorServiceCapabilities = new ArrayList<>();
            
            // Submit Callable and get Future
            Future<String> future = executorService.submit(() -> {
                Thread.sleep(100);
                return "Virtual thread task completed!";
            });
            
            String callableResult = future.get();
            executorServiceCapabilities.add("Got result from Callable: " + callableResult);
            
            // Submit Runnable with result
            Future<String> runnableFuture = executorService.submit(() -> {
                logger.debug("Runnable with result executed");
            }, "Runnable result");
            
            String runnableResult = runnableFuture.get();
            executorServiceCapabilities.add("Runnable result: " + runnableResult);
            
            // Batch operations
            List<Callable<String>> tasks = new ArrayList<>();
            tasks.add(() -> "Task 1");
            tasks.add(() -> "Task 2");
            tasks.add(() -> "Task 3");
            
            List<Future<String>> futures = executorService.invokeAll(tasks);
            executorServiceCapabilities.add("Batch results: " + futures.size() + " tasks completed");
            
            result.setExecutorServiceCapabilities(executorServiceCapabilities);
            
            // ====== LIFECYCLE MANAGEMENT ======
            List<String> lifecycleMethods = Arrays.asList(
                "shutdown() - graceful shutdown",
                "shutdownNow() - immediate shutdown", 
                "awaitTermination() - wait for completion",
                "isShutdown() - check status",
                "isTerminated() - check if all tasks finished"
            );
            result.setLifecycleMethods(lifecycleMethods);
            
            // ====== INTEGRATION WITH COMPLETABLEFUTURE ======
            CompletableFuture<Void> completableFuture = 
                CompletableFuture.runAsync(() -> {
                    logger.debug("CompletableFuture with ExecutorService");
                }, executorService);
                
            completableFuture.get(); // Wait for completion
            result.setCompletableFutureIntegration(true);
            
            // ====== WHY NOT JUST EXECUTOR? ======
            List<String> executorLimitationsWhyList = Arrays.asList(
                "No way to get task results",
                "No way to cancel tasks", 
                "No way to manage executor lifecycle",
                "No integration with Future APIs",
                "No batch operations"
            );
            result.setExecutorLimitationsWhy(executorLimitationsWhyList);
            
            List<String> executorServiceBenefits = Arrays.asList(
                "Complete task lifecycle management",
                "Integration with Java's Future framework",
                "Proper resource cleanup",
                "Production-ready concurrency patterns"
            );
            result.setExecutorServiceBenefits(executorServiceBenefits);
            
            // ====== SHUTDOWN EXECUTOR ======
            executorService.shutdown();
            boolean gracefulShutdown = executorService.awaitTermination(2, TimeUnit.SECONDS);
            
            if (gracefulShutdown) {
                result.setShutdownSuccess(true);
                result.setShutdownMessage("ExecutorService shut down gracefully");
                logger.info("ExecutorService shut down gracefully");
            } else {
                result.setShutdownSuccess(false);
                result.setShutdownMessage("Forced shutdown required");
                logger.warn("Forced shutdown required");
                executorService.shutdownNow();
            }
            
            result.setDemoCompleted(true);
            
        } catch (Exception e) {
            result.setDemoCompleted(false);
            result.setErrorMessage(e.getMessage());
            logger.error("Demo execution failed", e);
        }
        
        return result;
    }
    
    /**
     * Result class for the demo execution
     */
    public static class ExecutorDemoResult {
        private boolean executorCreated = false;
        private boolean demoCompleted = false;
        private boolean shutdownSuccess = false;
        private boolean completableFutureIntegration = false;
        private String shutdownMessage = "";
        private String errorMessage = "";
        private List<String> executorLimitations = new ArrayList<>();
        private List<String> executorLimitationsDetailed = new ArrayList<>();
        private List<String> executorServiceCapabilities = new ArrayList<>();
        private List<String> lifecycleMethods = new ArrayList<>();
        private List<String> executorLimitationsWhy = new ArrayList<>();
        private List<String> executorServiceBenefits = new ArrayList<>();
        
        // Getters and Setters
        public boolean isExecutorCreated() { return executorCreated; }
        public void setExecutorCreated(boolean executorCreated) { this.executorCreated = executorCreated; }
        
        public boolean isDemoCompleted() { return demoCompleted; }
        public void setDemoCompleted(boolean demoCompleted) { this.demoCompleted = demoCompleted; }
        
        public boolean isShutdownSuccess() { return shutdownSuccess; }
        public void setShutdownSuccess(boolean shutdownSuccess) { this.shutdownSuccess = shutdownSuccess; }
        
        public boolean isCompletableFutureIntegration() { return completableFutureIntegration; }
        public void setCompletableFutureIntegration(boolean completableFutureIntegration) { this.completableFutureIntegration = completableFutureIntegration; }
        
        public String getShutdownMessage() { return shutdownMessage; }
        public void setShutdownMessage(String shutdownMessage) { this.shutdownMessage = shutdownMessage; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public List<String> getExecutorLimitations() { return executorLimitations; }
        public void setExecutorLimitations(List<String> executorLimitations) { this.executorLimitations = executorLimitations; }
        
        public List<String> getExecutorLimitationsDetailed() { return executorLimitationsDetailed; }
        public void setExecutorLimitationsDetailed(List<String> executorLimitationsDetailed) { this.executorLimitationsDetailed = executorLimitationsDetailed; }
        
        public List<String> getExecutorServiceCapabilities() { return executorServiceCapabilities; }
        public void setExecutorServiceCapabilities(List<String> executorServiceCapabilities) { this.executorServiceCapabilities = executorServiceCapabilities; }
        
        public List<String> getLifecycleMethods() { return lifecycleMethods; }
        public void setLifecycleMethods(List<String> lifecycleMethods) { this.lifecycleMethods = lifecycleMethods; }
        
        public List<String> getExecutorLimitationsWhy() { return executorLimitationsWhy; }
        public void setExecutorLimitationsWhy(List<String> executorLimitationsWhy) { this.executorLimitationsWhy = executorLimitationsWhy; }
        
        public List<String> getExecutorServiceBenefits() { return executorServiceBenefits; }
        public void setExecutorServiceBenefits(List<String> executorServiceBenefits) { this.executorServiceBenefits = executorServiceBenefits; }
    }
}
