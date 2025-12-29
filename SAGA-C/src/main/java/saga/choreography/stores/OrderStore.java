package saga.choreography.stores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderStore {
    public enum Status { CREATED, INVENTORY_RESERVED, PAYMENT_SUCCEEDED, CANCELLED }

    private final Map<String, Status> store = new ConcurrentHashMap<>();

    public void create(String orderId) {
        store.put(orderId, Status.CREATED);
        System.out.println("OrderStore: created " + orderId);
    }

    public void update(String orderId, Status status) {
        store.put(orderId, status);
        System.out.println("OrderStore: order " + orderId + " -> " + status);
    }

    public Status get(String orderId) {
        return store.get(orderId);
    }
}
