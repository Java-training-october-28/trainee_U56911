package SAGA;

import SAGA.commands.CommandType;
import SAGA.commands.SagaCommand;
import SAGA.events.EventType;
import SAGA.events.SagaEvent;

import java.util.UUID;

/**
 * SAGA Orchestration Pattern Demo - Demonstrates the flow of commands and events
 * between the orchestrator and services (without actual Kafka, just simulation).
 * 
 * This demo shows the exact same flow that would happen in a Spring Boot + Kafka
 * implementation, but using simple method calls for demonstration.
 */
public class SagaOrchestrationDemo {
    
    public static void main(String[] args) {
        System.out.println("\n====== SAGA ORCHESTRATION PATTERN DEMO ======\n");
        System.out.println("Architecture: Central Orchestrator controls 3 microservices");
        System.out.println("Flow: Order -> Payment -> Inventory (with compensation on failures)\n");
        
        // Simulate three scenarios
        runScenario("Scenario 1: Successful Order", false, false);
        runScenario("Scenario 2: Payment Failure", true, false);
        runScenario("Scenario 3: Inventory Failure", false, true);
        
        System.out.println("\n====== END OF DEMO ======\n");
    }
    
    private static void runScenario(String title, boolean failPayment, boolean failInventory) {
        System.out.println("\n--- " + title + " ---");
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("[Orchestrator] Starting saga for order: " + orderId);
        
        // Step 1: Create Order
        System.out.println("[Orchestrator] Sending CREATE_ORDER command to OrderService");
        System.out.println("[OrderService] Creating order: " + orderId);
        System.out.println("[OrderService] Emitting ORDER_CREATED event");
        
        // Orchestrator receives ORDER_CREATED
        System.out.println("[Orchestrator] Received ORDER_CREATED event");
        System.out.println("[Orchestrator] Sending PROCESS_PAYMENT command to PaymentService");
        
        // Step 2: Process Payment
        System.out.println("[PaymentService] Processing payment for order: " + orderId);
        if (failPayment) {
            System.out.println("[PaymentService] Payment failed: insufficient funds");
            System.out.println("[PaymentService] Emitting PAYMENT_FAILED event");
            System.out.println("[Orchestrator] Received PAYMENT_FAILED event");
            System.out.println("[Orchestrator] Sending CANCEL_ORDER command to OrderService");
            System.out.println("[OrderService] Cancelling order: " + orderId);
            System.out.println("[OrderService] Emitting ORDER_CANCELLED event");
            System.out.println("[Orchestrator] Saga completed with failure - Order cancelled");
            return;
        }
        
        System.out.println("[PaymentService] Payment successful");
        System.out.println("[PaymentService] Emitting PAYMENT_SUCCESS event");
        System.out.println("[Orchestrator] Received PAYMENT_SUCCESS event");
        System.out.println("[Orchestrator] Sending RESERVE_INVENTORY command to InventoryService");
        
        // Step 3: Reserve Inventory
        System.out.println("[InventoryService] Reserving inventory for order: " + orderId);
        if (failInventory) {
            System.out.println("[InventoryService] Inventory reservation failed: out of stock");
            System.out.println("[InventoryService] Emitting INVENTORY_FAILED event");
            System.out.println("[Orchestrator] Received INVENTORY_FAILED event");
            System.out.println("[Orchestrator] Starting compensation...");
            System.out.println("[Orchestrator] Sending REFUND_PAYMENT command to PaymentService");
            System.out.println("[PaymentService] Refunding payment for order: " + orderId);
            System.out.println("[Orchestrator] Sending CANCEL_ORDER command to OrderService");
            System.out.println("[OrderService] Cancelling order: " + orderId);
            System.out.println("[OrderService] Emitting ORDER_CANCELLED event");
            System.out.println("[Orchestrator] Compensation completed");
            return;
        }
        
        System.out.println("[InventoryService] Inventory reserved successfully");
        System.out.println("[InventoryService] Emitting INVENTORY_RESERVED event");
        System.out.println("[Orchestrator] Received INVENTORY_RESERVED event");
        System.out.println("[Orchestrator] Sending CONFIRM_ORDER command to OrderService");
        System.out.println("[OrderService] Confirming order: " + orderId);
        System.out.println("[OrderService] Emitting ORDER_CONFIRMED event");
        System.out.println("[Orchestrator] Saga completed successfully - Order confirmed");
    }
}
