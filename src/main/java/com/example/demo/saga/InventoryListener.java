package com.example.demo.saga;

import com.example.demo.saga.OrderEvents.InventoryFailedEvent;
import com.example.demo.saga.OrderEvents.InventoryReleasedEvent;
import com.example.demo.saga.OrderEvents.InventoryReservedEvent;
import com.example.demo.saga.OrderEvents.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InventoryListener {
    private static final Logger log = LoggerFactory.getLogger(InventoryListener.class);
    private final KafkaTemplate<String, Object> kafka;
    private final ReservationStore reservationStore;
    private final OrderStore orderStore;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryListener(KafkaTemplate<String, Object> kafka, ReservationStore reservationStore, OrderStore orderStore) {
        this.kafka = kafka;
        this.reservationStore = reservationStore;
        this.orderStore = orderStore;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "inventory-group", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderEvents(Envelope env) {
        try {
            switch (env.eventType) {
                case KafkaTopics.TYPE_ORDER_CREATED -> {
                    OrderCreatedEvent ev = mapper.convertValue(env.payload, OrderCreatedEvent.class);
                    log.info("Inventory: received ORDER_CREATED {}", ev.orderId);
                    boolean reserved = tryReserve(ev.productId, ev.quantity);
                    if (reserved) {
                        reservationStore.save(ev.orderId, ev.productId, ev.quantity);
                        orderStore.setStatus(ev.orderId, OrderStore.Status.RESERVED);
                        var out = new InventoryReservedEvent(ev.orderId, ev.productId, ev.quantity);
                        kafka.send(KafkaTopics.ORDER_EVENTS, ev.orderId.toString(), new Envelope(KafkaTopics.TYPE_INVENTORY_RESERVED, ev.orderId, out));
                    } else {
                        var out = new InventoryFailedEvent(ev.orderId, "out_of_stock");
                        kafka.send(KafkaTopics.ORDER_EVENTS, ev.orderId.toString(), new Envelope(KafkaTopics.TYPE_INVENTORY_FAILED, ev.orderId, out));
                    }
                }
                case KafkaTopics.TYPE_PAYMENT_FAILED -> {
                    var evt = mapper.convertValue(env.payload, OrderEvents.PaymentFailedEvent.class);
                    log.info("Inventory: payment failed for {} -> compensating", evt.orderId);
                    reservationStore.find(evt.orderId).ifPresent(r -> {
                        var out = new InventoryReleasedEvent(evt.orderId, r.productId, r.quantity, evt.reason);
                        kafka.send(KafkaTopics.ORDER_EVENTS, evt.orderId.toString(), new Envelope(KafkaTopics.TYPE_INVENTORY_RELEASED, evt.orderId, out));
                        reservationStore.remove(evt.orderId);
                    });
                    orderStore.setStatus(evt.orderId, OrderStore.Status.CANCELLED);
                }
                default -> log.debug("Inventory: ignoring event type {}", env.eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process order event", e);
        }
    }

    private boolean tryReserve(Long productId, int qty) {
        // demo logic: 80% success
        return Math.random() > 0.2;
    }
}
