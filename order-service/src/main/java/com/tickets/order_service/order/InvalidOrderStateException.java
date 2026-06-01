package com.tickets.order_service.order;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(Long orderId, Order.Status status) {
        super("Order " + orderId + " cannot be paid, current status: " + status);
    }
}