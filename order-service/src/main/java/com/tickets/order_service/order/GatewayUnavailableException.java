package com.tickets.order_service.order;

public class GatewayUnavailableException extends RuntimeException {
    public GatewayUnavailableException(Long eventId) {
        super();
    }
}
