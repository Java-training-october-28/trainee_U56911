package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

// Exclude Kafka and Cloud Stream auto-configurations
@SpringBootApplication(exclude = { 
    KafkaAutoConfiguration.class,
    IntegrationAutoConfiguration.class
})
@EnableCaching
@EnableScheduling  // Enable for OutboxScheduler
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
