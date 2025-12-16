package com.example.demo.saga;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderProducer {
    private final KafkaTemplate<String, Object> kafka;

    public OrderProducer(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void publishOrderCreated(UUID orderId, Long productId, int quantity, double amount) {
        var ev = new OrderEvents.OrderCreatedEvent(orderId, productId, quantity, amount);
        var envelope = new Envelope(KafkaTopics.TYPE_ORDER_CREATED, orderId, ev);
        kafka.send(KafkaTopics.ORDER_EVENTS, orderId.toString(), envelope);
    }
}
