package com.example.demo.saga;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ReservationStore {
    private final Map<UUID, Reservation> store = new ConcurrentHashMap<>();

    public void save(UUID orderId, Long productId, int quantity) {
        store.put(orderId, new Reservation(productId, quantity));
    }

    public Optional<Reservation> find(UUID orderId) {
        return Optional.ofNullable(store.get(orderId));
    }

    public void remove(UUID orderId) {
        store.remove(orderId);
    }

    public static class Reservation {
        public final Long productId;
        public final int quantity;

        public Reservation(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
