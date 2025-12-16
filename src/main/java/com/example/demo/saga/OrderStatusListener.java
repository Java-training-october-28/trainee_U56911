package com.example.demo.saga;

import com.example.demo.saga.OrderEvents.InventoryFailedEvent;
import com.example.demo.saga.OrderEvents.InventoryReleasedEvent;
import com.example.demo.saga.OrderEvents.PaymentFailedEvent;
import com.example.demo.saga.OrderEvents.PaymentSucceededEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderStatusListener {
    private static final Logger log = LoggerFactory.getLogger(OrderStatusListener.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "orders-group", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderEvents(Envelope env) {
        try {
            switch (env.eventType) {
                case KafkaTopics.TYPE_PAYMENT_SUCCEEDED -> {
                    PaymentSucceededEvent ev = mapper.convertValue(env.payload, PaymentSucceededEvent.class);
                    log.info("Order {} completed (payment succeeded)", ev.orderId);
                }
                case KafkaTopics.TYPE_INVENTORY_FAILED -> {
                    InventoryFailedEvent ev = mapper.convertValue(env.payload, InventoryFailedEvent.class);
                    log.info("Order {} cancelled (inventory failed={})", ev.orderId, ev.reason);
                }
                case KafkaTopics.TYPE_PAYMENT_FAILED -> {
                    PaymentFailedEvent ev = mapper.convertValue(env.payload, PaymentFailedEvent.class);
                    log.info("Order {} cancelled (payment failed={})", ev.orderId, ev.reason);
                }
                case KafkaTopics.TYPE_INVENTORY_RELEASED -> {
                    InventoryReleasedEvent ev = mapper.convertValue(env.payload, InventoryReleasedEvent.class);
                    log.info("Inventory released for order {} (product={} qty={}) reason={}", ev.orderId, ev.productId, ev.quantity, ev.reason);
                }
                default -> log.debug("Orders: ignoring event type {}", env.eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process order event in orders listener", e);
        }
    }
}
