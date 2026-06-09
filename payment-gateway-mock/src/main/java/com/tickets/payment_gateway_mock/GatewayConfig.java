package com.tickets.payment_gateway_mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayConfig {
    public volatile double failureRate;
    public volatile long latencyMs;
    public volatile double declineRate;
    public volatile long webhookDelayMs;
    public volatile double confirmRate;   // for PIX/boleto: fraction that confirm vs expires
    public final String orderServiceUrl;

    public GatewayConfig(
            @Value("${gateway.failure-rate}") double failureRate,
            @Value("${gateway.latency-ms}") long latencyMs,
            @Value("${gateway.decline-rate}") double declineRate,
            @Value("${gateway.webhook-delay-ms}") long webhookDelayMs,
            @Value("${gateway.confirm-rate}") double confirmRate,
            @Value("${gateway.order-service-url}") String orderServiceUrl) {
        this.failureRate = failureRate;
        this.latencyMs = latencyMs;
        this.declineRate = declineRate;
        this.webhookDelayMs = webhookDelayMs;
        this.confirmRate = confirmRate;
        this.orderServiceUrl = orderServiceUrl;
    }
}