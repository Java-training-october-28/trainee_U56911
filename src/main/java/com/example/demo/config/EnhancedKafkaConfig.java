package com.example.demo.config;

import com.example.demo.dto.KafkaTaskMessageDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced Kafka Configuration for Training Purposes
 * Demonstrates:
 * 1. Kafka topics, partitions, replication configuration
 * 2. Producer and Consumer implementation
 * 3. Async Kafka listener configuration
 * 4. Dead Letter Topic handling
 * 5. Failure handling and retries
 */
@Configuration
public class EnhancedKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ==================== PRODUCER CONFIGURATION ====================
    
    /**
     * Producer Factory with advanced configuration for training
     * Demonstrates:
     * - ACKS configuration (0, 1, all)
     * - Retries and idempotent producer
     * - Compression
     * - Batch size and linger.ms
     */
    @Bean
    public ProducerFactory<String, Object> enhancedProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Training: Producer reliability configurations
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Retry 3 times
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100); // Backoff 100ms
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Idempotent producer
        
        // Training: Performance optimizations
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Compression
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 16KB batch size
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5); // Wait up to 5ms for batching
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32MB buffer
        
        // Training: Timeout configurations
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000); // Max block time 60s
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // Delivery timeout 120s
        
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Kafka Template for sending messages
     * Demonstrates transactional producer
     */
    @Bean
    public KafkaTemplate<String, Object> enhancedKafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(enhancedProducerFactory());
        template.setTransactionIdPrefix("tx-"); // Enable transactions
        return template;
    }

    // ==================== CONSUMER CONFIGURATION ====================
    
    /**
     * Consumer Factory with error handling deserializer
     * Demonstrates:
     * - Error handling deserializer for malformed messages
     * - Manual offset control
     * - Heartbeat configuration
     */
    @Bean
    public ConsumerFactory<String, KafkaTaskMessageDTO> enhancedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // Training: Use ErrorHandlingDeserializer for robust message handling
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        // Training: Consumer group and offset configuration
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "task-management-training-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start from beginning
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit for training
        
        // Training: Performance and reliability
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500); // Max records per poll
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 minute timeout
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000); // 10 second session timeout
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // 3 second heartbeat
        
        // Training: Fetch configuration
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1); // Minimum bytes to fetch
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Max wait time
        
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

    // ==================== LISTENER CONTAINER FACTORY ====================
    
    /**
     * Concurrent Kafka Listener Container Factory with Dead Letter Topic handling
     * Demonstrates:
     * - Concurrency for parallel processing
     * - Dead Letter Topic for failed messages
     * - Retry mechanism with backoff
     * - Manual acknowledgment
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> 
            enhancedKafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(enhancedConsumerFactory());
        
        // Training: Concurrency for parallel processing
        factory.setConcurrency(3); // 3 concurrent consumers
        
        // Training: Manual acknowledgment for control
        factory.getContainerProperties().setAckMode(
            org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE
        );
        
        // Training: Poll timeout configuration
        factory.getContainerProperties().setPollTimeout(3000);
        
        // Training: Setup Dead Letter Topic handling
        DeadLetterPublishingRecoverer dlqRecoverer = new DeadLetterPublishingRecoverer(
            enhancedKafkaTemplate(),
            (record, exception) -> {
                // Training: Custom logic for DLQ topic naming
                return new org.springframework.kafka.support.KafkaHeaders(
                    record.topic() + ".DLQ", 
                    record.partition()
                );
            }
        );
        
        // Training: Error handler with retries and DLQ
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            dlqRecoverer,
            new FixedBackOff(1000L, 2) // Retry 2 times with 1 second delay
        );
        
        // Training: Configure which exceptions to retry
        errorHandler.addNotRetryableExceptions(
            java.lang.IllegalArgumentException.class
        );
        
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            // Training: Log retry attempts for monitoring
            System.out.printf("Retry attempt %d for record: %s%n", 
                deliveryAttempt, record.value());
        });
        
        factory.setCommonErrorHandler(errorHandler);
        
        return factory;
    }

    // ==================== ASYNC LISTENER CONFIGURATION ====================
    
    /**
     * Async listener container factory for non-blocking processing
     * Demonstrates async message processing patterns
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> 
            asyncKafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(enhancedConsumerFactory());
        
        // Training: Higher concurrency for async processing
        factory.setConcurrency(5);
        
        // Training: Async processing with CompletableFuture
        factory.setBatchListener(false);
        
        // Training: Configure for async error handling
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new FixedBackOff(1000L, 1)
        ));
        
        return factory;
    }

    // ==================== BATCH PROCESSING CONFIGURATION ====================
    
    /**
     * Batch listener container factory for processing messages in batches
     * Demonstrates batch processing efficiency
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> 
            batchKafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, KafkaTaskMessageDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        Map<String, Object> batchProps = new HashMap<>(enhancedConsumerFactory().getConfigurationProperties());
        batchProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000); // Larger batch size
        batchProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800); // 50MB max fetch
        
        ConsumerFactory<String, KafkaTaskMessageDTO> batchConsumerFactory = 
            new DefaultKafkaConsumerFactory<>(
                batchProps,
                new StringDeserializer(),
                new JsonDeserializer<>(KafkaTaskMessageDTO.class)
            );
        
        factory.setConsumerFactory(batchConsumerFactory);
        factory.setBatchListener(true); // Enable batch listening
        factory.setConcurrency(2);
        
        return factory;
    }
}