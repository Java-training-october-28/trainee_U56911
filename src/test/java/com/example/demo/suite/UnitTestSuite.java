package com.example.demo.suite;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all unit tests
 */
@Suite
@SelectPackages({
    "com.example.demo.service",
    "com.example.demo.controller",
    "com.example.demo.mapper"
})
@IncludeClassNamePatterns(".*Test")
public class UnitTestSuite {
}