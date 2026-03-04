package com.example.demo.config;

import com.example.demo.saga.Envelope;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * Transactional Producer Factory
     * 
     * IMPORTANT: For Kafka transactions to work, we must:
     * 1. Set TRANSACTIONAL_ID_CONFIG - enables exactly-once semantics
     * 2. Set ACKS_CONFIG to 'all' - ensures durability
     * 
     * This allows us to:
     * - Send messages within a transaction
     * - Rollback Kafka messages if DB transaction fails
     * - Participate in distributed transactions with ChainedKafkaTransactionManager
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // TRANSACTIONAL_ID_CONFIG - enables Kafka transactions
        // Each instance must have a unique transaction id
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "demo-transactional-producer");
        
        // ACKS_CONFIG = 'all' ensures messages are replicated before acknowledgment
        // Required for reliable transactions
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // Retry configuration for transaction reliability
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        return new DefaultKafkaProducerFactory<>(props);
    }
    
    /**
     * Kafka Transaction Manager
     * 
     * Manages Kafka transactions independently.
     * Used when you need Kafka-only transactions or as part of ChainedKafkaTransactionManager.
     * 
     * Key points:
     * - Starts a transaction before any Kafka operation
     * - Commits transaction when method completes successfully
     * - Rolls back transaction if any exception occurs
     */
    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }
    
    /**
     * Chained Transaction Manager (Database + Kafka)
     * 
     * This is the KEY IMPLEMENTATION for distributed transactions!
     * 
     * How it works:
     * 1. Begins DB transaction first
     * 2. Begins Kafka transaction second
     * 3. When method succeeds: commits Kafka first, then DB
     * 4. When method fails: rolls back both (in reverse order)
     * 
     * CRITICAL: If Kafka commit fails after DB commit, 
     * ChainedKafkaTransactionManager will mark for rollback.
     * 
     * @param dbTxManager - The JPA/Database transaction manager (auto-configured by Spring Boot)
     * @param kafkaTxManager - Our Kafka transaction manager
     * @return Chained transaction manager that handles both DB and Kafka
     */
    @Bean
    public ChainedKafkaTransactionManager<String, Object> chainedTransactionManager(
            JpaTransactionManager dbTxManager,
            KafkaTransactionManager<String, Object> kafkaTxManager) {
        // Order matters! Kafka first, then DB
        // This ensures Kafka messages are sent before DB commits
        return new ChainedKafkaTransactionManager<>(kafkaTxManager, dbTxManager);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, Envelope> consumerFactory() {
        JsonDeserializer<Envelope> deserializer = new JsonDeserializer<>(Envelope.class);
        deserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());
        // group id can be overridden in @KafkaListener

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Envelope> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Envelope> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(4);
        factory.getContainers().forEach(container -> container.setPollTimeout(3000));
        return factory;
    }
}
