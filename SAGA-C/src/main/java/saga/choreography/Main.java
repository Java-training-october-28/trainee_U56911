package saga.choreography;

import saga.choreography.services.InventoryService;
import saga.choreography.services.OrderProducer;
import saga.choreography.services.OrderStatusService;
import saga.choreography.services.PaymentService;
import saga.choreography.stores.OrderStore;
import saga.choreography.stores.ReservationStore;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        EventBus bus = new EventBus();
        ReservationStore reservationStore = new ReservationStore();
        OrderStore orderStore = new OrderStore();

        // wire services
        new InventoryService(bus, reservationStore);
        new PaymentService(bus);
        new OrderStatusService(bus, orderStore);

        // producer
        OrderProducer producer = new OrderProducer(bus);

        // create a few orders to show flow
        producer.createOrder("order-1");
        Thread.sleep(500);
        producer.createOrder("order-2");
        Thread.sleep(1000);

        System.out.println("Demo complete. Exiting.");
        bus.shutdown();
    }
}
