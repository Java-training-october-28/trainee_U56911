package com.example.demo.saga;

import com.example.demo.saga.OrderEvents.InventoryReservedEvent;
import com.example.demo.saga.OrderEvents.PaymentFailedEvent;
import com.example.demo.saga.OrderEvents.PaymentSucceededEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentListener.class);
    private final KafkaTemplate<String, Object> kafka;
    private final OrderStore orderStore;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentListener(KafkaTemplate<String, Object> kafka, OrderStore orderStore) {
        this.kafka = kafka;
        this.orderStore = orderStore;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS, groupId = "payment-group", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderEvents(Envelope env) {
        try {
            if (KafkaTopics.TYPE_INVENTORY_RESERVED.equals(env.eventType)) {
                InventoryReservedEvent ev = mapper.convertValue(env.payload, InventoryReservedEvent.class);
                log.info("Payment: processing payment for {}", ev.orderId);
                boolean paid = attemptPayment(ev.orderId, /*amount*/ 100.0);
                if (paid) {
                    var out = new PaymentSucceededEvent(ev.orderId, 100.0);
                    kafka.send(KafkaTopics.ORDER_EVENTS, ev.orderId.toString(), new Envelope(KafkaTopics.TYPE_PAYMENT_SUCCEEDED, ev.orderId, out));
                    orderStore.setStatus(ev.orderId, OrderStore.Status.PAYMENT_SUCCEEDED);
                } else {
                    var out = new PaymentFailedEvent(ev.orderId, "card_declined");
                    kafka.send(KafkaTopics.ORDER_EVENTS, ev.orderId.toString(), new Envelope(KafkaTopics.TYPE_PAYMENT_FAILED, ev.orderId, out));
                    orderStore.setStatus(ev.orderId, OrderStore.Status.CANCELLED);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process order event for payment", e);
        }
    }

    private boolean attemptPayment(java.util.UUID orderId, double amount) {
        // demo: 70% success
        return Math.random() > 0.3;
    }
}
