package saga.choreography.stores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReservationStore {
    private final Map<String, Integer> reservations = new ConcurrentHashMap<>();

    public boolean reserve(String productId, int qty) {
        // simple simulation: if qty <= 5 allow, else fail
        if (qty <= 5) {
            reservations.put(productId, reservations.getOrDefault(productId, 0) + qty);
            System.out.println("ReservationStore: reserved " + qty + " of " + productId);
            return true;
        }
        System.out.println("ReservationStore: cannot reserve " + qty + " of " + productId);
        return false;
    }

    public void release(String productId, int qty) {
        reservations.computeIfPresent(productId, (k, v) -> Math.max(0, v - qty));
        System.out.println("ReservationStore: released " + qty + " of " + productId);
    }
}
