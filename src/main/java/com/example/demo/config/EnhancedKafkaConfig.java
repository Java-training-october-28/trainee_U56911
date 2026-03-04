package com.example.demo.config;

import com.example.demo.dto.KafkaTaskMessageDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced Kafka Configuration for Training Purposes
 * 
 * KEY CONCEPT: Most Kafka config is now in application.yaml!
 * This class demonstrates:
 * 1. Custom error handling deserializer
 * 2. Multiple container factories for different use cases
 * 3. Bean-based configuration when needed
 * 
 * Spring Boot auto-configures most properties from application.yaml
 */
@Configuration
public class EnhancedKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ==================== CUSTOM CONSUMER FACTORY ====================
    
    /**
     * Custom Consumer Factory with ErrorHandlingDeserializer
     * 
     * Why this is needed:
     * - Handles malformed JSON messages gracefully
     * - Prevents listener from crashing on bad data
     * - Logs errors instead of failing the entire consumer
     */
    @Bean
    public ConsumerFactory<String, KafkaTaskMessageDTO> enhancedConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // ErrorHandlingDeserializer wraps the JSON deserializer
        // If deserialization fails, it passes the error to the error handler instead of crashing
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        // Copy properties from application.yaml
        props.putAll(kafkaProperties.getConsumer().getProperties());
        
        // Configure JsonDeserializer
        JsonDeserializer<KafkaTaskMessageDTO> jsonDeserializer = new JsonDeserializer<>(KafkaTaskMessageDTO.class);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeMapperForKey(false);
        
        return new DefaultKafkaConsumerFactory<>(
            props, 
            new StringDeserializer(), 
            jsonDeserializer
        );
    }

    // ==================== DEFAULT LISTENER CONTAINER FACTORY ====================
    
    /**
     * Main listener container factory
     * Uses concurrency from application.yaml (spring.kafka.listener.concurrency)
     * Uses ack-mode from application.yaml (spring.kafka.listener.ack-mode)
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> 
            enhancedKafkaListenerContainerFactory(ConsumerFactory<String, KafkaTaskMessageDTO> enhancedConsumerFactory) {
        
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(enhancedConsumerFactory);
        
        // Note: Concurrency and ack-mode now come from application.yaml
        // spring.kafka.listener.concurrency=3
        // spring.kafka.listener.ack-mode=manual_immediate
        
        return factory;
    }

    // ==================== ASYNC LISTENER FACTORY ====================
    
    /**
     * Async listener container factory for non-blocking processing
     * Uses higher concurrency for async scenarios
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> 
            asyncKafkaListenerContainerFactory(ConsumerFactory<String, KafkaTaskMessageDTO> enhancedConsumerFactory) {
        
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(enhancedConsumerFactory);
        factory.setConcurrency(5);  // Higher concurrency for async
        factory.setBatchListener(false);
        
        return factory;
    }

    // ==================== BATCH PROCESSING FACTORY ====================
    
    /**
     * Batch listener container factory for processing messages in batches
     * Optimized for high-throughput scenarios
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> 
            batchKafkaListenerContainerFactory(KafkaProperties kafkaProperties) {
        
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        // Override for batch processing
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);  // Larger batches
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800); // 50MB
        
        JsonDeserializer<KafkaTaskMessageDTO> jsonDeserializer = new JsonDeserializer<>(KafkaTaskMessageDTO.class);
        jsonDeserializer.addTrustedPackages("*");
        
        ConsumerFactory<String, KafkaTaskMessageDTO> batchConsumerFactory = 
            new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
        
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(batchConsumerFactory);
        factory.setBatchListener(true);  // Enable batch mode
        factory.setConcurrency(2);
        
        return factory;
    }
}
