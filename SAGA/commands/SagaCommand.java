package SAGA.commands;

public class SagaCommand {
    private final String orderId;
    private final CommandType type;
    private final Object payload;

    public SagaCommand(String orderId, CommandType type, Object payload) {
        this.orderId = orderId;
        this.type = type;
        this.payload = payload;
    }

    public String getOrderId() {
        return orderId;
    }

    public CommandType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "SagaCommand{" +
                "orderId='" + orderId + '\'' +
                ", type=" + type +
                ", payload=" + payload +
                '}';
    }
}
