package com.tickets.order_service.order;

public class PaymentDeclinedException extends RuntimeException {
    public PaymentDeclinedException(Long orderId) {
        super("Payment declined for order: " + orderId);
    }
}