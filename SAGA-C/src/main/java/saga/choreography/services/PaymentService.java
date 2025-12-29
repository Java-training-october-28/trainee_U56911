package saga.choreography.services;

import saga.choreography.Envelope;
import saga.choreography.EventBus;
import saga.choreography.events.OrderEvents.InventoryReserved;
import saga.choreography.events.OrderEvents.PaymentFailed;
import saga.choreography.events.OrderEvents.PaymentSucceeded;

public class PaymentService {
    private final EventBus bus;

    public static final String TYPE_PAYMENT_SUCCEEDED = "PaymentSucceeded";
    public static final String TYPE_PAYMENT_FAILED = "PaymentFailed";

    public PaymentService(EventBus bus) {
        this.bus = bus;
        this.bus.subscribe(this::onEvent);
    }

    private void onEvent(Envelope env) {
        try {
            if (InventoryService.TYPE_INVENTORY_RESERVED.equals(env.eventType)) {
                InventoryReserved r = (InventoryReserved) env.payload;
                System.out.println("PaymentService: received InventoryReserved for " + r.orderId);
                // simulate payment outcome
                boolean ok = Math.random() > 0.2; // 80% success
                if (ok) {
                    PaymentSucceeded s = new PaymentSucceeded(r.orderId, 42.0);
                    System.out.println("PaymentService: publishing PaymentSucceeded for " + r.orderId);
                    bus.publish(new Envelope(TYPE_PAYMENT_SUCCEEDED, r.orderId, s));
                } else {
                    PaymentFailed f = new PaymentFailed(r.orderId, "card-declined");
                    System.out.println("PaymentService: publishing PaymentFailed for " + r.orderId);
                    bus.publish(new Envelope(TYPE_PAYMENT_FAILED, r.orderId, f));
                }
            }
        } catch (Exception e) {
            System.err.println("PaymentService error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
