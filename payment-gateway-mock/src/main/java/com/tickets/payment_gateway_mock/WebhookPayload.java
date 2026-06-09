package com.tickets.payment_gateway_mock;

public record WebhookPayload(
    String chargeId,
    Long orderId,
    String status,          // CONFIRMED or EXPIRED
    String idempotencyKey
) {}