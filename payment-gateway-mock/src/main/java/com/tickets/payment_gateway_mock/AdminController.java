package com.tickets.payment_gateway_mock;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final GatewayConfig config;

    public AdminController(GatewayConfig config) {
        this.config = config;
    }

    @PostMapping("/mode")
    public Map<String, Object> setMode(@RequestBody Map<String, Object> body) {
        if (body.containsKey("failureRate"))
            config.failureRate = ((Number) body.get("failureRate")).doubleValue();
        if (body.containsKey("latencyMs"))
            config.latencyMs = ((Number) body.get("latencyMs")).longValue();
        if (body.containsKey("declineRate"))
            config.declineRate = ((Number) body.get("declineRate")).doubleValue();
        if (body.containsKey("webhookDelayMs"))
            config.webhookDelayMs = ((Number) body.get("webhookDelayMs")).longValue();
        if (body.containsKey("confirmRate"))
            config.confirmRate = ((Number) body.get("confirmRate")).doubleValue();

        return getMode();
    }

    @GetMapping("/mode")
    public Map<String, Object> getMode() {
        return Map.of(
            "failureRate", config.failureRate,
            "latencyMs", config.latencyMs,
            "declineRate", config.declineRate,
            "webhookDelayMs", config.webhookDelayMs,
            "confirmRate", config.confirmRate
        );
    }
}