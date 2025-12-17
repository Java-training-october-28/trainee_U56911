package SAGA.services;

import SAGA.commands.CommandType;
import SAGA.commands.SagaCommand;
import SAGA.events.EventType;
import SAGA.events.SagaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class PaymentService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();

    public PaymentService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-commands", groupId = "payment-service")
    public void handle(SagaCommand cmd) {
        System.out.println("[PaymentService] Received command: " + cmd);

        if (cmd.getType() == CommandType.PROCESS_PAYMENT) {
            try {
                processPayment(cmd.getOrderId());
                emit(EventType.PAYMENT_SUCCESS, cmd.getOrderId());
            } catch (Exception ex) {
                emit(EventType.PAYMENT_FAILED, cmd.getOrderId());
            }
        }

        if (cmd.getType() == CommandType.REFUND_PAYMENT) {
            refund(cmd.getOrderId());
        }
    }

    private void processPayment(String orderId) {
        System.out.println("[PaymentService] Processing payment for order: " + orderId);
        // Simulate payment processing with random failure (20% chance)
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate random failure for demo
        if (random.nextInt(5) == 0) { // 20% chance of failure
            throw new RuntimeException("Payment declined: insufficient funds");
        }
    }

    private void refund(String orderId) {
        System.out.println("[PaymentService] Refunding payment for order: " + orderId);
        // Simulate refund
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void emit(EventType type, String orderId) {
        System.out.println("[PaymentService] Emitting event: " + type + " for order: " + orderId);
        kafkaTemplate.send(
            "order-events",
            new SagaEvent(orderId, type, null)
        );
    }
}
