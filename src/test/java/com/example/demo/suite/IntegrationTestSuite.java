package com.example.demo.suite;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all integration tests
 */
@Suite
@SelectPackages({
    "com.example.demo.repository",
    "com.example.demo.integration",
    "com.example.demo.performance"
})
@IncludeClassNamePatterns(".*IntegrationTest|.*PerformanceTest")
public class IntegrationTestSuite {
}