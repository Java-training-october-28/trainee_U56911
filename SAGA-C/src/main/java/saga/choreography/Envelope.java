package saga.choreography;

public class Envelope {
    public final String eventType;
    public final String orderId;
    public final Object payload;

    public Envelope(String eventType, String orderId, Object payload) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Envelope{" +
                "eventType='" + eventType + '\'' +
                ", orderId='" + orderId + '\'' +
                ", payload=" + payload +
                '}';
    }
}
