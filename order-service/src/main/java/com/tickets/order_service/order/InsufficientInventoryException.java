package com.tickets.order_service.order;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(Long eventId) {
        super("No tickets available for event: " + eventId);
    }
}