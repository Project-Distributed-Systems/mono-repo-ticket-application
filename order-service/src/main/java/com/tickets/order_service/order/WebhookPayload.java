package com.tickets.order_service.order;

public record WebhookPayload(String chargeId, Long orderId, String status, String idempotencyKey) {}