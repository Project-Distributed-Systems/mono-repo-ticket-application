package com.tickets.payment_gateway_mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // mutable refs so the demo can flip them live
    private volatile double failureRate;
    private volatile long latencyMs;
    private volatile double declineRate;

    // inject initial values from config
    public AdminController(
            @Value("${gateway.failure-rate}") double failureRate,
            @Value("${gateway.latency-ms}") long latencyMs,
            @Value("${gateway.decline-rate}") double declineRate) {
        this.failureRate = failureRate;
        this.latencyMs = latencyMs;
        this.declineRate = declineRate;
    }

    @PostMapping("/mode")
    public Map<String, Object> setMode(@RequestBody Map<String, Object> body) {
        if (body.containsKey("failureRate"))
            this.failureRate = ((Number) body.get("failureRate")).doubleValue();
        if (body.containsKey("latencyMs"))
            this.latencyMs = ((Number) body.get("latencyMs")).longValue();
        if (body.containsKey("declineRate"))
            this.declineRate = ((Number) body.get("declineRate")).doubleValue();

        return Map.of("failureRate", failureRate, "latencyMs", latencyMs, "declineRate", declineRate);
    }

    @GetMapping("/mode")
    public Map<String, Object> getMode() {
        return Map.of("failureRate", failureRate, "latencyMs", latencyMs, "declineRate", declineRate);
    }
}