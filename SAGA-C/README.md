Saga Choreography demo
======================

This is a minimal, self-contained Java demo that illustrates the saga choreography pattern.
It uses an in-memory EventBus to simulate asynchronous events between services (Order -> Inventory -> Payment).

How to run (requires JDK 11+):

Windows (PowerShell):

    javac -d out SAGA-C/src/main/java/saga/choreography/*.java
    java -cp out saga.choreography.Main

This will print the event flow to the console.

Files:
- `Main.java` - entrypoint, wires components and emits an OrderCreated event
- `EventBus.java` - tiny async event bus
- `Envelope.java` - generic envelope carrying eventType/orderId/payload
- `events/` - event DTO classes
- `services/` - InventoryService, PaymentService, OrderProducer, OrderStatusService
- `stores/` - in-memory OrderStore and ReservationStore

Purpose: educational demo — no external dependencies, no Spring Boot.
