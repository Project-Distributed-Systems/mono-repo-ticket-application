package com.tickets.payment_gateway_mock;

public record ChargeRequest(
    String idempotencyKey,
    String paymentMethod,   // CREDIT_CARD, PIX, BOLETO
    Long orderId,
    java.math.BigDecimal amount
) {}