package com.example.demo.performance;

import com.example.demo.base.BaseIntegrationTest;
import com.example.demo.dto.auth.AuthRegisterDTO;
import com.example.demo.entity.Role;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationPerformanceTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerEndpoint_ShouldHandleSequentialRequests() throws Exception {
        // Given
        int numberOfRequests = 10;
        StopWatch stopWatch = new StopWatch();
        
        // When
        stopWatch.start();
        for (int i = 0; i < numberOfRequests; i++) {
            AuthRegisterDTO registerDTO = new AuthRegisterDTO(
                "user" + i,
                "user" + i + "@example.com",
                "password123",
                Role.USER
            );

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDTO)))
                    .andExpect(status().isCreated());
        }
        stopWatch.stop();

        // Then
        long totalTime = stopWatch.getTotalTimeMillis();
        double averageTime = (double) totalTime / numberOfRequests;
        
        System.out.println("Sequential Registration Performance:");
        System.out.println("Total requests: " + numberOfRequests);
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average time per request: " + averageTime + "ms");
        
        // Assert reasonable performance (adjust thresholds as needed)
        assertTrue(averageTime < 1000, "Average registration time should be less than 1 second");
        assertTrue(totalTime < 10000, "Total time for 10 registrations should be less than 10 seconds");
    }

    @Test
    void registerEndpoint_ShouldHandleConcurrentRequests() throws Exception {
        // Given
        int numberOfThreads = 5;
        int requestsPerThread = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<Future<Long>> futures = new ArrayList<>();

        // When
        StopWatch totalStopWatch = new StopWatch();
        totalStopWatch.start();

        for (int thread = 0; thread < numberOfThreads; thread++) {
            final int threadId = thread;
            Future<Long> future = executor.submit(() -> {
                try {
                    StopWatch threadStopWatch = new StopWatch();
                    threadStopWatch.start();
                    
                    for (int i = 0; i < requestsPerThread; i++) {
                        AuthRegisterDTO registerDTO = new AuthRegisterDTO(
                            "thread" + threadId + "_user" + i,
                            "thread" + threadId + "_user" + i + "@example.com",
                            "password123",
                            Role.USER
                        );

                        mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDTO)))
                                .andExpect(status().isCreated());
                    }
                    
                    threadStopWatch.stop();
                    return threadStopWatch.getTotalTimeMillis();
                } catch (Exception e) {
                    fail("Thread " + threadId + " failed: " + e.getMessage());
                    return -1L;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        // Wait for all threads to complete
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        totalStopWatch.stop();
        executor.shutdown();

        // Then
        assertTrue(completed, "All threads should complete within 30 seconds");
        
        long totalTime = totalStopWatch.getTotalTimeMillis();
        int totalRequests = numberOfThreads * requestsPerThread;
        double averageTime = (double) totalTime / totalRequests;
        
        System.out.println("Concurrent Registration Performance:");
        System.out.println("Threads: " + numberOfThreads);
        System.out.println("Requests per thread: " + requestsPerThread);
        System.out.println("Total requests: " + totalRequests);
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average time per request: " + averageTime + "ms");
        
        // Check individual thread performance
        for (int i = 0; i < futures.size(); i++) {
            try {
                Long threadTime = futures.get(i).get();
                assertTrue(threadTime > 0, "Thread " + i + " should have positive execution time");
                System.out.println("Thread " + i + " time: " + threadTime + "ms");
            } catch (ExecutionException | InterruptedException e) {
                fail("Failed to get thread " + i + " result: " + e.getMessage());
            }
        }

        // Assert reasonable concurrent performance
        assertTrue(averageTime < 2000, "Average concurrent registration time should be reasonable");
        assertEquals(totalRequests, userRepository.count(), "All users should be created");
    }

    @Test
    void passwordEncodingPerformance_ShouldBeReasonable() throws Exception {
        // Given
        int numberOfRequests = 5;
        StopWatch stopWatch = new StopWatch();
        List<Long> times = new ArrayList<>();

        // When - Test password encoding performance through registration
        for (int i = 0; i < numberOfRequests; i++) {
            AuthRegisterDTO registerDTO = new AuthRegisterDTO(
                "perfuser" + i,
                "perfuser" + i + "@example.com",
                "verylongpasswordtotestencryptionperformance123456789",
                Role.USER
            );

            stopWatch.start();
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDTO)))
                    .andExpect(status().isCreated());
            stopWatch.stop();
            
            times.add(stopWatch.getLastTaskTimeMillis());
        }

        // Then
        long totalTime = stopWatch.getTotalTimeMillis();
        double averageTime = (double) totalTime / numberOfRequests;
        long maxTime = times.stream().mapToLong(Long::longValue).max().orElse(0L);
        long minTime = times.stream().mapToLong(Long::longValue).min().orElse(0L);
        
        System.out.println("Password Encoding Performance:");
        System.out.println("Total requests: " + numberOfRequests);
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average time: " + averageTime + "ms");
        System.out.println("Min time: " + minTime + "ms");
        System.out.println("Max time: " + maxTime + "ms");
        
        // Assert that password encoding doesn't take too long
        assertTrue(maxTime < 5000, "Maximum registration time should be less than 5 seconds");
        assertTrue(averageTime < 2000, "Average registration time should be less than 2 seconds");
    }

    @Test
    void databaseConnectionPool_ShouldHandleMultipleConnections() throws Exception {
        // Given
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<Future<Boolean>> futures = new ArrayList<>();

        // When
        for (int thread = 0; thread < numberOfThreads; thread++) {
            final int threadId = thread;
            Future<Boolean> future = executor.submit(() -> {
                try {
                    AuthRegisterDTO registerDTO = new AuthRegisterDTO(
                        "dbpool" + threadId,
                        "dbpool" + threadId + "@example.com",
                        "password123",
                        Role.USER
                    );

                    mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerDTO)))
                            .andExpect(status().isCreated());
                    
                    return true;
                } catch (Exception e) {
                    System.err.println("Thread " + threadId + " failed: " + e.getMessage());
                    return false;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        // Wait for completion
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        assertTrue(completed, "All database operations should complete");
        
        // Verify all operations succeeded
        for (int i = 0; i < futures.size(); i++) {
            try {
                Boolean success = futures.get(i).get();
                assertTrue(success, "Database operation " + i + " should succeed");
            } catch (ExecutionException | InterruptedException e) {
                fail("Database operation " + i + " failed: " + e.getMessage());
            }
        }

        // Verify all users were created
        assertEquals(numberOfThreads, userRepository.count());
    }

    @Test
    void memoryUsage_ShouldBeReasonableForMultipleRequests() throws Exception {
        // Given
        Runtime runtime = Runtime.getRuntime();
        int numberOfRequests = 20;
        
        // Measure initial memory
        runtime.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // When - Create multiple users
        for (int i = 0; i < numberOfRequests; i++) {
            AuthRegisterDTO registerDTO = new AuthRegisterDTO(
                "memtest" + i,
                "memtest" + i + "@example.com",
                "password123",
                Role.USER
            );

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDTO)))
                    .andExpect(status().isCreated());
        }
        
        // Measure final memory
        runtime.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        // Then
        System.out.println("Memory Usage Analysis:");
        System.out.println("Initial memory: " + (initialMemory / 1024 / 1024) + " MB");
        System.out.println("Final memory: " + (finalMemory / 1024 / 1024) + " MB");
        System.out.println("Memory increase: " + (memoryIncrease / 1024 / 1024) + " MB");
        System.out.println("Memory per request: " + (memoryIncrease / numberOfRequests / 1024) + " KB");
        
        // Assert reasonable memory usage (adjust threshold as needed)
        long maxMemoryIncrease = 50 * 1024 * 1024; // 50 MB
        assertTrue(memoryIncrease < maxMemoryIncrease, 
            "Memory increase should be less than 50MB for " + numberOfRequests + " requests");
    }
}