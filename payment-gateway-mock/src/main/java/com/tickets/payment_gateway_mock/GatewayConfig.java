package com.tickets.payment_gateway_mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayConfig {
    public volatile double failureRate;
    public volatile long latencyMs;
    public volatile double declineRate;

    public GatewayConfig(
            @Value("${gateway.failure-rate}") double failureRate,
            @Value("${gateway.latency-ms}") long latencyMs,
            @Value("${gateway.decline-rate}") double declineRate) {
        this.failureRate = failureRate;
        this.latencyMs = latencyMs;
        this.declineRate = declineRate;
    }
}