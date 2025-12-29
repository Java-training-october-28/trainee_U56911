package saga.choreography.events;

import java.util.List;

public class OrderEvents {
    public static class OrderCreated {
        public final String orderId;
        public final List<String> items;
        public final double amount;

        public OrderCreated(String orderId, List<String> items, double amount) {
            this.orderId = orderId;
            this.items = items;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "OrderCreated{" +
                    "orderId='" + orderId + '\'' +
                    ", items=" + items +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class InventoryReserved {
        public final String orderId;
        public final String productId;
        public final int quantity;

        public InventoryReserved(String orderId, String productId, int quantity) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "InventoryReserved{" +
                    "orderId='" + orderId + '\'' +
                    ", productId='" + productId + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    public static class InventoryFailed {
        public final String orderId;
        public final String reason;

        public InventoryFailed(String orderId, String reason) {
            this.orderId = orderId;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "InventoryFailed{" +
                    "orderId='" + orderId + '\'' +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }

    public static class PaymentSucceeded {
        public final String orderId;
        public final double amount;

        public PaymentSucceeded(String orderId, double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "PaymentSucceeded{" +
                    "orderId='" + orderId + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class PaymentFailed {
        public final String orderId;
        public final String reason;

        public PaymentFailed(String orderId, String reason) {
            this.orderId = orderId;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return "PaymentFailed{" +
                    "orderId='" + orderId + '\'' +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }
}
