package com.example.demo.saga;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Envelope {
    public final String eventType;
    public final UUID orderId;
    public final Object payload;

    @JsonCreator
    public Envelope(@JsonProperty("eventType") String eventType,
                    @JsonProperty("orderId") UUID orderId,
                    @JsonProperty("payload") Object payload) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.payload = payload;
    }
}
