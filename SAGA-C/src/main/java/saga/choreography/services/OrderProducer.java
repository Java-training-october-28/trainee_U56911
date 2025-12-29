package saga.choreography.services;

import saga.choreography.Envelope;
import saga.choreography.EventBus;
import saga.choreography.events.OrderEvents.OrderCreated;

import java.util.Arrays;

public class OrderProducer {
    private final EventBus bus;

    public static final String TYPE_ORDER_CREATED = "OrderCreated";

    public OrderProducer(EventBus bus) {
        this.bus = bus;
    }

    public void createOrder(String orderId) {
        // sample order
        OrderCreated oc = new OrderCreated(orderId, Arrays.asList("sku-1"), 42.0);
        System.out.println("OrderProducer: publishing OrderCreated for " + orderId);
        bus.publish(new Envelope(TYPE_ORDER_CREATED, orderId, oc));
    }
}
