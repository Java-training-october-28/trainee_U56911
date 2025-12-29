package saga.choreography;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventBus {
    private final List<Consumer<Envelope>> subscribers = new ArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void publish(Envelope env) {
        // async dispatch to all subscribers
        for (Consumer<Envelope> s : subscribers) {
            executor.submit(() -> {
                try {
                    s.accept(env);
                } catch (Exception e) {
                    System.err.println("Subscriber error: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    public void subscribe(Consumer<Envelope> consumer) {
        subscribers.add(consumer);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
