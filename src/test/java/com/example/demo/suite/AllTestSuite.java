package com.example.demo.suite;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Complete test suite for all tests
 */
@Suite
@SelectPackages("com.example.demo")
@IncludeClassNamePatterns(".*Test")
public class AllTestSuite {
}