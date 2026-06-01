package com.tickets.order_service.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody CreateOrderRequest req) {
        Order order = service.createOrder(req.userId(), req.eventId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<Order> findAll() {
        return service.findAll();
    }
}