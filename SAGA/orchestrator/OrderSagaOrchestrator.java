package SAGA.orchestrator;

import SAGA.commands.CommandType;
import SAGA.commands.SagaCommand;
import SAGA.events.EventType;
import SAGA.events.SagaEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderSagaOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderSagaOrchestrator(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Start saga
    public void startSaga(String orderId) {
        System.out.println("[SAGA] Starting saga for order: " + orderId);
        kafkaTemplate.send(
            "order-commands",
            new SagaCommand(orderId, CommandType.CREATE_ORDER, null)
        );
    }

    @KafkaListener(topics = "order-events", groupId = "orchestrator")
    public void handleEvent(SagaEvent event) {
        System.out.println("[SAGA] Received event: " + event);

        switch (event.getType()) {
            case ORDER_CREATED:
                System.out.println("[SAGA] Order created, processing payment...");
                send(CommandType.PROCESS_PAYMENT, event.getOrderId());
                break;

            case ORDER_CREATE_FAILED:
                System.out.println("[SAGA] Order creation failed: " + event.getReason());
                markSagaFailed(event.getOrderId(), event.getReason());
                break;

            case PAYMENT_SUCCESS:
                System.out.println("[SAGA] Payment successful, reserving inventory...");
                send(CommandType.RESERVE_INVENTORY, event.getOrderId());
                break;

            case PAYMENT_FAILED:
                System.out.println("[SAGA] Payment failed, cancelling order...");
                send(CommandType.CANCEL_ORDER, event.getOrderId());
                break;

            case INVENTORY_RESERVED:
                System.out.println("[SAGA] Inventory reserved, confirming order...");
                send(CommandType.CONFIRM_ORDER, event.getOrderId());
                break;

            case INVENTORY_FAILED:
                System.out.println("[SAGA] Inventory failed, compensating...");
                send(CommandType.REFUND_PAYMENT, event.getOrderId());
                send(CommandType.CANCEL_ORDER, event.getOrderId());
                break;
        }
    }

    private void send(CommandType type, String orderId) {
        System.out.println("[SAGA] Sending command: " + type + " for order: " + orderId);
        kafkaTemplate.send(
            "order-commands",
            new SagaCommand(orderId, type, null)
        );
    }

    private void markSagaFailed(String orderId, String reason) {
        System.out.println("[SAGA] Marking saga as failed for order: " + orderId + " reason: " + reason);
        // In production: update saga state in database, send notifications, etc.
    }
}
