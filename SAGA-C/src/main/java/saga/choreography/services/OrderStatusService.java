package saga.choreography.services;

import saga.choreography.Envelope;
import saga.choreography.EventBus;
import saga.choreography.events.OrderEvents.InventoryFailed;
import saga.choreography.events.OrderEvents.InventoryReserved;
import saga.choreography.events.OrderEvents.PaymentFailed;
import saga.choreography.events.OrderEvents.PaymentSucceeded;
import saga.choreography.stores.OrderStore;

public class OrderStatusService {
    private final EventBus bus;
    private final OrderStore store;

    public OrderStatusService(EventBus bus, OrderStore store) {
        this.bus = bus;
        this.store = store;
        this.bus.subscribe(this::onEvent);
    }

    private void onEvent(Envelope env) {
        try {
            switch (env.eventType) {
                case OrderProducer.TYPE_ORDER_CREATED -> {
                    store.create(env.orderId);
                }
                case InventoryService.TYPE_INVENTORY_RESERVED -> {
                    InventoryReserved r = (InventoryReserved) env.payload;
                    store.update(r.orderId, OrderStore.Status.INVENTORY_RESERVED);
                }
                case PaymentService.TYPE_PAYMENT_SUCCEEDED -> {
                    PaymentSucceeded p = (PaymentSucceeded) env.payload;
                    store.update(p.orderId, OrderStore.Status.PAYMENT_SUCCEEDED);
                }
                case InventoryService.TYPE_INVENTORY_FAILED, PaymentService.TYPE_PAYMENT_FAILED -> {
                    System.out.println("OrderStatusService: saga failed for " + env.orderId + " -> cancelling");
                    store.update(env.orderId, OrderStore.Status.CANCELLED);
                }
                default -> System.out.println("OrderStatusService: ignoring " + env.eventType);
            }
        } catch (Exception e) {
            System.err.println("OrderStatusService error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
