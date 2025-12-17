package SAGA;

import SAGA.orchestrator.SagaOrchestrator;
import SAGA.orchestrator.SagaOrchestrator.SagaResult;

import java.math.BigDecimal;

/**
 * SAGA Pattern Demo - Demonstrates orchestration with 3 microservices
 * 
 * Microservices:
 * 1. OrderCreateService - Creates orders
 * 2. PaymentProcessService - Processes payments
 * 3. InventoryReserveService - Reserves inventory
 * 
 * Run this class to see SAGA orchestration in action with success and failure scenarios.
 */
public class SagaDemo {
    
    public static void main(String[] args) {
        SagaOrchestrator orchestrator = new SagaOrchestrator();
        
        System.out.println("\n====== SAGA PATTERN DEMO ======\n");
        
        // Scenario 1: Successful Order
        System.out.println("\n--- Scenario 1: Successful Order ---");
        SagaResult result1 = orchestrator.executeSaga("PROD-001", 5, new BigDecimal("500"));
        System.out.println("Result: " + result1);
        
        // Scenario 2: Payment Failure (amount > 10000)
        System.out.println("\n--- Scenario 2: Payment Failure ---");
        SagaResult result2 = orchestrator.executeSaga("PROD-001", 5, new BigDecimal("15000"));
        System.out.println("Result: " + result2);
        
        // Scenario 3: Inventory Failure (insufficient stock)
        System.out.println("\n--- Scenario 3: Inventory Failure ---");
        SagaResult result3 = orchestrator.executeSaga("PROD-003", 100, new BigDecimal("500"));
        System.out.println("Result: " + result3);
        
        System.out.println("\n====== END OF DEMO ======\n");
    }
}
