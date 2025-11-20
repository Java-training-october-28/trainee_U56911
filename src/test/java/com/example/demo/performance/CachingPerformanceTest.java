package com.example.demo.performance;

import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance tests for caching implementation
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CachingPerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(CachingPerformanceTest.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        // Create test data
        testUser = new User("cacheuser", "cache@example.com", "password", Role.USER);
        testUser = userRepository.save(testUser);

        testProject = new Project("Cache Test Project", "Performance test for caching", testUser);
        testProject = projectRepository.save(testProject);
    }

    @Test
    void testUserCachePerformance() {
        logger.info("Testing user cache performance...");

        // First call - should hit database
        long startTime = System.nanoTime();
        var user1 = userService.getUserById(testUser.getId());
        long firstCallTime = System.nanoTime() - startTime;

        // Second call - should hit cache
        startTime = System.nanoTime();
        var user2 = userService.getUserById(testUser.getId());
        long secondCallTime = System.nanoTime() - startTime;

        assertThat(user1).isNotNull();
        assertThat(user2).isNotNull();
        assertThat(user1.getId()).isEqualTo(user2.getId());

        logger.info("First call (database): {} ns", firstCallTime);
        logger.info("Second call (cache): {} ns", secondCallTime);

        // Cache should be significantly faster
        double speedup = (double) firstCallTime / secondCallTime;
        logger.info("Cache speedup: {}x", String.format("%.2f", speedup));

        assertThat(secondCallTime).isLessThan(firstCallTime);
        assertThat(speedup).isGreaterThan(2.0); // Cache should be at least 2x faster
    }

    @Test
    void testProjectCachePerformance() {
        logger.info("Testing project cache performance...");

        // First call - should hit database
        long startTime = System.nanoTime();
        var project1 = projectService.getProjectById(testProject.getId());
        long firstCallTime = System.nanoTime() - startTime;

        // Second call - should hit cache
        startTime = System.nanoTime();
        var project2 = projectService.getProjectById(testProject.getId());
        long secondCallTime = System.nanoTime() - startTime;

        assertThat(project1).isNotNull();
        assertThat(project2).isNotNull();
        assertThat(project1.getId()).isEqualTo(project2.getId());

        logger.info("First call (database): {} ns", firstCallTime);
        logger.info("Second call (cache): {} ns", secondCallTime);

        // Cache should be significantly faster
        double speedup = (double) firstCallTime / secondCallTime;
        logger.info("Cache speedup: {}x", String.format("%.2f", speedup));

        assertThat(secondCallTime).isLessThan(firstCallTime);
        assertThat(speedup).isGreaterThan(2.0); // Cache should be at least 2x faster
    }

    @Test
    void testCacheEvictionPerformance() {
        logger.info("Testing cache eviction performance...");

        // Populate cache
        var user1 = userService.getUserById(testUser.getId());
        var project1 = projectService.getProjectById(testProject.getId());

        // Update user to trigger cache eviction
        long startTime = System.nanoTime();
        var updateDTO = new com.example.demo.dto.UserUpdateDTO();
        updateDTO.setUsername("updateduser");
        userService.updateUser(testUser.getId(), updateDTO);
        long updateTime = System.nanoTime() - startTime;

        // Get user again - should hit database due to cache eviction
        startTime = System.nanoTime();
        var user2 = userService.getUserById(testUser.getId());
        long postUpdateTime = System.nanoTime() - startTime;

        logger.info("Update operation time: {} ns", updateTime);
        logger.info("Post-update fetch time: {} ns", postUpdateTime);

        assertThat(user2).isNotNull();
        assertThat(user2.getUsername()).isEqualTo("updateduser");
    }

    @Test
    void testConcurrentCacheAccess() throws InterruptedException {
        logger.info("Testing concurrent cache access...");

        int threadCount = 10;
        int iterations = 100;
        Thread[] threads = new Thread[threadCount];
        long[] totalTimes = new long[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                long threadStartTime = System.nanoTime();
                for (int j = 0; j < iterations; j++) {
                    userService.getUserById(testUser.getId());
                }
                totalTimes[threadIndex] = System.nanoTime() - threadStartTime;
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Calculate statistics
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;

        for (long time : totalTimes) {
            totalTime += time;
            minTime = Math.min(minTime, time);
            maxTime = Math.max(maxTime, time);
        }

        long avgTime = totalTime / threadCount;

        logger.info("Concurrent cache access results:");
        logger.info("Threads: {}, Iterations per thread: {}", threadCount, iterations);
        logger.info("Average time per thread: {} ns", avgTime);
        logger.info("Min time: {} ns", minTime);
        logger.info("Max time: {} ns", maxTime);

        // All threads should complete successfully
        assertThat(minTime).isGreaterThan(0);
        assertThat(maxTime).isLessThan(TimeUnit.SECONDS.toNanos(10)); // Should complete within 10 seconds
    }

    @Test
    void testCacheHitRate() {
        logger.info("Testing cache hit rate...");

        int totalRequests = 100;
        int cacheMisses = 0;
        int cacheHits = 0;

        for (int i = 0; i < totalRequests; i++) {
            long startTime = System.nanoTime();
            var user = userService.getUserById(testUser.getId());
            long duration = System.nanoTime() - startTime;

            // Rough heuristic: cache hits are typically much faster
            if (i == 0 || duration > TimeUnit.MICROSECONDS.toNanos(100)) {
                cacheMisses++;
            } else {
                cacheHits++;
            }
        }

        double hitRate = (double) cacheHits / totalRequests;
        logger.info("Cache hit rate: {}%", String.format("%.2f", hitRate * 100));
        logger.info("Cache hits: {}, Cache misses: {}", cacheHits, cacheMisses);

        // After first request, hit rate should be high
        assertThat(hitRate).isGreaterThan(0.8); // At least 80% hit rate after warmup
    }
}
