package SAGA.services;

import SAGA.commands.CommandType;
import SAGA.commands.SagaCommand;
import SAGA.events.EventType;
import SAGA.events.SagaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-commands", groupId = "order-service")
    public void handle(SagaCommand cmd) {
        System.out.println("[OrderService] Received command: " + cmd);

        if (cmd.getType() == CommandType.CREATE_ORDER) {
            try {
                createOrder(cmd.getOrderId());
                emit(EventType.ORDER_CREATED, cmd.getOrderId(), null);
            } catch (Exception ex) {
                emit(EventType.ORDER_CREATE_FAILED, cmd.getOrderId(), ex.getMessage());
            }
        }

        if (cmd.getType() == CommandType.CANCEL_ORDER) {
            cancelOrder(cmd.getOrderId());
            emit(EventType.ORDER_CANCELLED, cmd.getOrderId(), null);
        }

        if (cmd.getType() == CommandType.CONFIRM_ORDER) {
            confirmOrder(cmd.getOrderId());
            emit(EventType.ORDER_CONFIRMED, cmd.getOrderId(), null);
        }
    }

    private void createOrder(String orderId) {
        System.out.println("[OrderService] Creating order: " + orderId);
        // Simulate order creation with random failure (10% chance)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate random failure for demo
        if (Math.random() < 0.1) { // 10% chance of failure
            throw new RuntimeException("Order creation failed: database constraint violation");
        }
    }

    private void cancelOrder(String orderId) {
        System.out.println("[OrderService] Cancelling order: " + orderId);
        // Simulate order cancellation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void confirmOrder(String orderId) {
        System.out.println("[OrderService] Confirming order: " + orderId);
        // Simulate order confirmation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void emit(EventType type, String orderId, String reason) {
        System.out.println("[OrderService] Emitting event: " + type + " for order: " + orderId + (reason != null ? " reason: " + reason : ""));
        kafkaTemplate.send(
            "order-events",
            new SagaEvent(orderId, type, reason)
        );
    }
}
