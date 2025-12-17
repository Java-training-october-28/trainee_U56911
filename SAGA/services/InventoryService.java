package SAGA.services;

import SAGA.commands.CommandType;
import SAGA.commands.SagaCommand;
import SAGA.events.EventType;
import SAGA.events.SagaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class InventoryService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();

    public InventoryService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-commands", groupId = "inventory-service")
    public void handle(SagaCommand cmd) {
        System.out.println("[InventoryService] Received command: " + cmd);

        if (cmd.getType() == CommandType.RESERVE_INVENTORY) {
            try {
                reserve(cmd.getOrderId());
                emit(EventType.INVENTORY_RESERVED, cmd.getOrderId());
            } catch (Exception ex) {
                emit(EventType.INVENTORY_FAILED, cmd.getOrderId());
            }
        }
    }

    private void reserve(String orderId) {
        System.out.println("[InventoryService] Reserving inventory for order: " + orderId);
        // Simulate inventory reservation with random failure (25% chance)
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate random failure for demo
        if (random.nextInt(4) == 0) { // 25% chance of failure
            throw new RuntimeException("Inventory reservation failed: out of stock");
        }
    }

    private void emit(EventType type, String orderId) {
        System.out.println("[InventoryService] Emitting event: " + type + " for order: " + orderId);
        kafkaTemplate.send(
            "order-events",
            new SagaEvent(orderId, type, null)
        );
    }
}
