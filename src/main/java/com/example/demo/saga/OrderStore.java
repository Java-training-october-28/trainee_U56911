package com.example.demo.saga;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderStore {
    public enum Status { CREATED, RESERVED, PAYMENT_SUCCEEDED, CANCELLED }

    private final Map<UUID, Status> store = new ConcurrentHashMap<>();

    public void setStatus(UUID orderId, Status status) {
        store.put(orderId, status);
    }

    public Optional<Status> getStatus(UUID orderId) {
        return Optional.ofNullable(store.get(orderId));
    }
}
