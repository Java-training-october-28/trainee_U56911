package SAGA;

import SAGA.commands.CommandType;
import SAGA.commands.SagaCommand;
import SAGA.events.EventType;
import SAGA.events.SagaEvent;

import java.util.UUID;

/**
 * SAGA Orchestration Pattern Demo V2 - Demonstrates the COMPLETE flow with failure handling
 * Shows how the orchestrator handles ORDER_CREATE_FAILED events (critical fix).
 * 
 * This demo shows the exact same flow that would happen in a Spring Boot + Kafka
 * implementation, but using simple method calls for demonstration.
 */
public class SagaOrchestrationDemoV2 {
    
    public static void main(String[] args) {
        System.out.println("\n====== SAGA ORCHESTRATION PATTERN DEMO V2 ======\n");
        System.out.println("Key Improvement: Every command now produces SUCCESS or FAILURE event");
        System.out.println("No more stuck sagas - orchestrator always gets a response\n");
        
        // Simulate four scenarios
        runScenario("Scenario 1: Successful Order", false, false, false);
        runScenario("Scenario 2: Order Creation Failure", true, false, false);
        runScenario("Scenario 3: Payment Failure", false, true, false);
        runScenario("Scenario 4: Inventory Failure", false, false, true);
        
        System.out.println("\n====== END OF DEMO ======\n");
    }
    
    private static void runScenario(String title, boolean failOrderCreate, boolean failPayment, boolean failInventory) {
        System.out.println("\n--- " + title + " ---");
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("[Orchestrator] Starting saga for order: " + orderId);
        
        // Step 1: Create Order
        System.out.println("[Orchestrator] Sending CREATE_ORDER command to OrderService");
        System.out.println("[OrderService] Creating order: " + orderId);
        
        if (failOrderCreate) {
            System.out.println("[OrderService] Order creation failed: database constraint violation");
            System.out.println("[OrderService] Emitting ORDER_CREATE_FAILED event");
            System.out.println("[Orchestrator] Received ORDER_CREATE_FAILED event");
            System.out.println("[Orchestrator] Marking saga as failed for order: " + orderId + " reason: Order creation failed: database constraint violation");
            System.out.println("[Orchestrator] Saga terminated early - no compensation needed");
            return;
        }
        
        System.out.println("[OrderService] Order created successfully");
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
