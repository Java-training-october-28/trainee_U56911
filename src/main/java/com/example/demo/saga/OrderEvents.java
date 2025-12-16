package com.example.demo.saga;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class OrderEvents {

    public static class OrderCreatedEvent {
        public final UUID orderId;
        public final Long productId;
        public final int quantity;
        public final double amount;

        @JsonCreator
        public OrderCreatedEvent(@JsonProperty("orderId") UUID orderId,
                                 @JsonProperty("productId") Long productId,
                                 @JsonProperty("quantity") int quantity,
                                 @JsonProperty("amount") double amount) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
            this.amount = amount;
        }
    }

    public static class InventoryReservedEvent {
        public final UUID orderId;
        public final Long productId;
        public final int quantity;

        @JsonCreator
        public InventoryReservedEvent(@JsonProperty("orderId") UUID orderId,
                                      @JsonProperty("productId") Long productId,
                                      @JsonProperty("quantity") int quantity) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
        }
    }

    public static class InventoryFailedEvent {
        public final UUID orderId;
        public final String reason;

        @JsonCreator
        public InventoryFailedEvent(@JsonProperty("orderId") UUID orderId,
                                    @JsonProperty("reason") String reason) {
            this.orderId = orderId;
            this.reason = reason;
        }
    }

    public static class InventoryReleasedEvent {
        public final UUID orderId;
        public final Long productId;
        public final int quantity;
        public final String reason;

        @JsonCreator
        public InventoryReleasedEvent(@JsonProperty("orderId") UUID orderId,
                                      @JsonProperty("productId") Long productId,
                                      @JsonProperty("quantity") int quantity,
                                      @JsonProperty("reason") String reason) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
            this.reason = reason;
        }
    }

    public static class PaymentSucceededEvent {
        public final UUID orderId;
        public final double amount;

        @JsonCreator
        public PaymentSucceededEvent(@JsonProperty("orderId") UUID orderId,
                                     @JsonProperty("amount") double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }
    }

    public static class PaymentFailedEvent {
        public final UUID orderId;
        public final String reason;

        @JsonCreator
        public PaymentFailedEvent(@JsonProperty("orderId") UUID orderId,
                                  @JsonProperty("reason") String reason) {
            this.orderId = orderId;
            this.reason = reason;
        }
    }
}
