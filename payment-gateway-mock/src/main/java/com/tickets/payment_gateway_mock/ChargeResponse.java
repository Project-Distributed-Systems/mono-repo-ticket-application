package com.tickets.payment_gateway_mock;

public record ChargeResponse(
    String chargeId,
    String status,   // APPROVED, DECLINED, PENDING
    String idempotencyKey
) {}