# Kafka Training Guide for Spring Boot Developers

## Overview
This guide provides comprehensive Kafka training materials for Spring Boot developers. The training covers event-driven communication, Kafka integration, failure handling, and Spring Cloud Stream.

## Training Objectives
- Implement event-driven communication patterns
- Configure Kafka integration with Spring Boot
- Handle failures and implement retries
- Demonstrate Kafka topics, partitions, and replication
- Show producer and consumer implementations
- Implement async Kafka listeners
- Integrate Spring Cloud Stream
- Handle Dead Letter Topics

## Training Structure

### 1. Basic Kafka Concepts
**Endpoint**: `POST /api/kafka-training/demonstrate-basics`
- Topics, partitions, producers, consumers
- Consumer groups and coordination
- Basic message sending demonstration

### 2. Producer Patterns
**Endpoint**: `POST /api/kafka-training/demonstrate-producers`
- Synchronous producers (blocking with timeout)
- Asynchronous producers (non-blocking with callbacks)
- Transactional producers (atomic message groups)
- Custom headers and metadata

### 3. Consumer Patterns
**Endpoint**: `GET /api/kafka-training/demonstrate-consumers`
- Traditional `@KafkaListener` with manual acknowledgment
- Async consumers with `CompletableFuture`
- Batch consumers for efficient processing
- Consumer group configurations

### 4. Event-Driven Architecture
**Endpoint**: `POST /api/kafka-training/demonstrate-event-driven`
- Domain events as business messages
- Event sourcing patterns
- CQRS (Command Query Responsibility Segregation)
- Event Carried State Transfer

### 5. Error Handling & Resilience
**Endpoint**: `POST /api/kafka-training/demonstrate-error-handling`
- Retry with exponential backoff
- Dead Letter Topics (DLQ) for failed messages
- Circuit breaker pattern
- Bulkhead pattern for isolation

### 6. Spring Cloud Stream Integration
**Endpoint**: `GET /api/kafka-training/demonstrate-spring-cloud-stream`
- Functional programming model (Supplier/Function/Consumer)
- Binder abstraction (Kafka, RabbitMQ, etc.)
- Declarative configuration
- Content negotiation (JSON, Avro, Protobuf)

### 7. Topics & Partitions
**Endpoint**: `GET /api/kafka-training/demonstrate-topics-partitions`
- Partitioning strategies (round robin, key-based, custom)
- Replication for fault tolerance
- Consumer assignment strategies
- Message ordering guarantees

### 8. Performance & Optimization
**Endpoint**: `GET /api/kafka-training/demonstrate-performance`
- Batching for efficiency
- Compression (snappy, gzip, lz4)
- Async producers for non-blocking sends
- Tuned timeouts and configurations

### 9. Monitoring & Observability
**Endpoint**: `GET /api/kafka-training/demonstrate-monitoring`
- Producer metrics (send rate, error rate, latency)
- Consumer metrics (lag, throughput, rebalances)
- Broker metrics (disk usage, network, CPU)
- Health checks and logging best practices

## Code Structure

### Enhanced Configuration
- `EnhancedKafkaConfig.java` - Advanced Kafka configuration with:
  - Producer reliability configurations (acks, retries, idempotence)
  - Consumer error handling with `ErrorHandlingDeserializer`
  - Dead Letter Topic handling with `DeadLetterPublishingRecoverer`
  - Multiple container factories for different use cases

### Enhanced Consumer Service
- `EnhancedKafkaConsumerService.java` - Demonstrates:
  - Traditional `@KafkaListener` with manual acknowledgment
  - Async processing with `CompletableFuture`
  - Batch processing for efficiency
  - Dead Letter Topic handling
  - Circuit breaker and retry patterns

### Enhanced Producer Service
- `EnhancedKafkaProducerService.java` - Demonstrates:
  - Synchronous and asynchronous sending
  - Transactional producers
  - Custom headers and partitioning
  - Event-driven domain events
  - Retry logic and error handling

### Training Controller
- `KafkaTrainingController.java` - REST endpoints for:
  - Interactive demonstrations of all concepts
  - Hands-on exercises for trainees
  - Understanding checks and quizzes
  - Comprehensive training summary

## Hands-On Exercises

### Exercise 1: Basic Message Flow
1. Send a message using synchronous producer
2. Consume the message with traditional listener
3. Observe partition assignment and offset

### Exercise 2: Error Handling
1. Send messages that will fail processing
2. Observe retry attempts in logs
3. Check Dead Letter Topic for failed messages
4. Implement custom error recovery

### Exercise 3: Performance Optimization
1. Configure batching and compression
2. Send batch of messages
3. Monitor throughput and latency
4. Tune producer/consumer configurations

### Exercise 4: Event-Driven Patterns
1. Publish domain events (created, updated, completed)
2. Implement event handlers for different event types
3. Demonstrate event sourcing concepts
4. Show CQRS pattern implementation

## Configuration Examples

### application.yaml Kafka Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      acks: all
      retries: 3
      compression-type: snappy
      batch-size: 16384
      linger-ms: 5
    consumer:
      group-id: task-management-training-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      max-poll-records: 500
```

### Spring Cloud Stream Configuration
```yaml
spring:
  cloud:
    stream:
      bindings:
        taskOutput:
          destination: task-events
          content-type: application/json
          group: task-management-group
        taskInput:
          destination: task-events
          content-type: application/json
          group: task-management-group
```

## Monitoring & Debugging

### Key Metrics to Monitor
1. **Producer Metrics**:
   - `kafka.producer:type=producer-metrics`
   - Record send rate, error rate, request latency

2. **Consumer Metrics**:
   - `kafka.consumer:type=consumer-metrics`
   - Records consumed, consumer lag, fetch rate

3. **Spring Boot Actuator**:
   - `/actuator/metrics` - Application metrics
   - `/actuator/health` - Health checks
   - `/actuator/env` - Environment properties

### Logging Configuration
```yaml
logging:
  level:
    org.apache.kafka: INFO
    org.springframework.kafka: DEBUG
    com.example.demo.service.EnhancedKafkaConsumerService: DEBUG
```

## Common Patterns & Best Practices

### 1. Idempotent Consumers
- Design consumers to handle duplicate messages
- Use database constraints or deduplication logic
- Implement idempotent operations

### 2. Exactly-Once Processing
- Use Kafka transactions for producers
- Implement idempotent consumers
- Consider using `enable.idempotence=true`

### 3. Schema Evolution
- Use Schema Registry for Avro/Protobuf
- Implement backward/forward compatibility
- Version your message schemas

### 4. Security
- Configure SSL/TLS for encryption
- Use SASL for authentication
- Implement ACLs for authorization

## Troubleshooting Guide

### Common Issues & Solutions

1. **Consumer Lag Increasing**
   - Check consumer processing speed
   - Increase consumer instances
   - Optimize processing logic

2. **Producer Timeouts**
   - Increase `max.block.ms`
   - Check broker availability
   - Review network connectivity

3. **Deserialization Errors**
   - Use `ErrorHandlingDeserializer`
   - Implement Dead Letter Topics
   - Validate message schemas

4. **Rebalance Issues**
   - Tune `session.timeout.ms`
   - Optimize `max.poll.interval.ms`
   - Implement cooperative rebalancing

## Assessment & Evaluation

### Understanding Check Questions
1. What is the difference between a topic and a partition?
2. How does Kafka ensure message ordering?
3. When would you use synchronous vs asynchronous producer?
4. What is a Dead Letter Topic and when is it used?
5. How does Spring Cloud Stream simplify Kafka integration?

### Practical Exercises
1. Implement a retry mechanism for failed messages
2. Build a simple event sourcing system
3. Configure monitoring for Kafka metrics
4. Implement circuit breaker pattern

## Resources & References

### Documentation
- [Kafka Official Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Documentation](https://spring.io/projects/spring-kafka)
- [Spring Cloud Stream Documentation](https://spring.io/projects/spring-cloud-stream)
- [Confluent Kafka Documentation](https://docs.confluent.io/platform/current/)

### Tools
- Kafka CLI tools (`kafka-topics`, `kafka-console-producer`, `kafka-console-consumer`)
- Kafka Manager/UI tools
- Prometheus & Grafana for monitoring
- JMX for metrics collection

### Sample Commands
```bash
# Create topic
kafka-topics --create --topic task-events --partitions 3 --replication-factor 1

# List topics
kafka-topics --list

# Produce messages
kafka-console-producer --topic task-events --broker-list localhost:9092

# Consume messages
kafka-console-consumer --topic task-events --from-beginning --bootstrap-server localhost:9092
```

## Next Steps

### Advanced Topics
1. **Kafka Streams** - Stream processing library
2. **Kafka Connect** - Data integration framework
3. **Schema Registry** - Schema management
4. **KSQL** - Streaming SQL engine

### Production Considerations
1. **High Availability** - Multi-broker clusters
2. **Disaster Recovery** - Cross-datacenter replication
3. **Security** - Encryption, authentication, authorization
4. **Monitoring** - Comprehensive observability

### Real-World Use Cases
1. **Event Sourcing** - State as sequence of events
2. **CQRS** - Separate read/write models
3. **Microservices Communication** - Async messaging
4. **Data Pipeline** - ETL and data integration

## Getting Started

### Prerequisites
1. Java 11+ and Maven/Gradle
2. Kafka cluster (local or Docker)
3. Spring Boot 2.7+ or 3.0+
4. Basic understanding of messaging concepts

### Setup Instructions
1. Clone the repository
2. Start Kafka cluster (Docker or local)
3. Configure application properties
4. Run the Spring Boot application
5. Access training endpoints at `http://localhost:8080/api/kafka-training`

### Testing the Implementation
```bash
# Test basic concepts
curl -X POST http://localhost:8080/api/kafka-training/demonstrate-basics

# Test producer patterns
curl -X POST http://localhost:8080/api/kafka-training/demonstrate-producers

# Test error handling
curl -X POST http://localhost:8080/api/kafka-training/demonstrate-error-handling
```

## Support & Feedback

For questions or feedback:
1. Review the code examples
2. Check application logs
3. Use the training endpoints
4. Refer to documentation links

This training guide provides comprehensive coverage of Kafka concepts with practical examples and hands-on exercises for Spring Boot developers.