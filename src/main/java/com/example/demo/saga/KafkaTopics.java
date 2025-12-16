package com.example.demo.saga;

public final class KafkaTopics {
    // Domain-level topic for saga events (single-topic approach)
    public static final String ORDER_EVENTS = "order.events";

    // Event type constants used inside the envelope
    public static final String TYPE_ORDER_CREATED = "ORDER_CREATED";
    public static final String TYPE_INVENTORY_RESERVED = "INVENTORY_RESERVED";
    public static final String TYPE_INVENTORY_FAILED = "INVENTORY_FAILED";
    public static final String TYPE_INVENTORY_RELEASED = "INVENTORY_RELEASED";
    public static final String TYPE_PAYMENT_SUCCEEDED = "PAYMENT_SUCCEEDED";
    public static final String TYPE_PAYMENT_FAILED = "PAYMENT_FAILED";

    private KafkaTopics() {}
}
