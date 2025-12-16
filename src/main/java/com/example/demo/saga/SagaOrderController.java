package com.example.demo.saga;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/saga/orders")
public class SagaOrderController {
    private final OrderProducer producer;

    public SagaOrderController(OrderProducer producer) { this.producer = producer; }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderDto dto) {
        UUID orderId = UUID.randomUUID();
        producer.publishOrderCreated(orderId, dto.productId(), dto.quantity(), dto.amount());
        return ResponseEntity.accepted().body(orderId.toString());
    }

    public record CreateOrderDto(Long productId, int quantity, double amount) {}
}
