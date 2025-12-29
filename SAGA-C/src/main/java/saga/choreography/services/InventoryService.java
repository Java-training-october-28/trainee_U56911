package saga.choreography.services;

import saga.choreography.Envelope;
import saga.choreography.EventBus;
import saga.choreography.events.OrderEvents.InventoryFailed;
import saga.choreography.events.OrderEvents.InventoryReserved;
import saga.choreography.events.OrderEvents.OrderCreated;
import saga.choreography.stores.ReservationStore;

public class InventoryService {
    private final EventBus bus;
    private final ReservationStore store;

    public static final String TYPE_INVENTORY_RESERVED = "InventoryReserved";
    public static final String TYPE_INVENTORY_FAILED = "InventoryFailed";

    public InventoryService(EventBus bus, ReservationStore store) {
        this.bus = bus;
        this.store = store;
        this.bus.subscribe(this::onEvent);
    }

    private void onEvent(Envelope env) {
        try {
            if (OrderProducer.TYPE_ORDER_CREATED.equals(env.eventType)) {
                OrderCreated oc = (OrderCreated) env.payload;
                System.out.println("InventoryService: received OrderCreated for " + oc.orderId);
                // simulate reserve
                boolean ok = store.reserve("product-1", 1);
                if (ok) {
                    InventoryReserved r = new InventoryReserved(oc.orderId, "product-1", 1);
                    System.out.println("InventoryService: publishing InventoryReserved for " + oc.orderId);
                    bus.publish(new Envelope(TYPE_INVENTORY_RESERVED, oc.orderId, r));
                } else {
                    InventoryFailed f = new InventoryFailed(oc.orderId, "not-enough-stock");
                    System.out.println("InventoryService: publishing InventoryFailed for " + oc.orderId);
                    bus.publish(new Envelope(TYPE_INVENTORY_FAILED, oc.orderId, f));
                }
            } else if (PaymentService.TYPE_PAYMENT_FAILED.equals(env.eventType)) {
                // on payment failure, release reservation
                InventoryFailed f = new InventoryFailed(env.orderId, "payment-failed");
                System.out.println("InventoryService: releasing inventory for " + env.orderId);
                store.release("product-1", 1);
                bus.publish(new Envelope(TYPE_INVENTORY_FAILED, env.orderId, f));
            }
        } catch (Exception e) {
            System.err.println("InventoryService error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
