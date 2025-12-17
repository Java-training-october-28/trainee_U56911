package SAGA.events;

public class SagaEvent {
    private final String orderId;
    private final EventType type;
    private final String reason;

    public SagaEvent(String orderId, EventType type, String reason) {
        this.orderId = orderId;
        this.type = type;
        this.reason = reason;
    }

    public String getOrderId() {
        return orderId;
    }

    public EventType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "SagaEvent{" +
                "orderId='" + orderId + '\'' +
                ", type=" + type +
                ", reason='" + reason + '\'' +
                '}';
    }
}
