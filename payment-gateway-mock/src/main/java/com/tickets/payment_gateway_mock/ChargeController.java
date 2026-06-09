package com.tickets.payment_gateway_mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/charges")
public class ChargeController {

    private static final Logger log = LoggerFactory.getLogger(ChargeController.class);

    private final Map<String, ChargeResponse> store = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    private final GatewayConfig config;
    private final RestTemplate restTemplate;

    public ChargeController(GatewayConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<ChargeResponse> charge(@RequestBody ChargeRequest req) throws InterruptedException {

        // idempotency = same key returns same result
        if (req.idempotencyKey() != null && store.containsKey(req.idempotencyKey())) {
            log.info("Idempotent hit for key {}", req.idempotencyKey());
            return ResponseEntity.ok(store.get(req.idempotencyKey()));
        }

        // simulate latency
        Thread.sleep(config.latencyMs);

        // simulate technical failure (503)
        if (Math.random() < config.failureRate) {
            log.warn("Simulating technical failure for order {}", req.orderId());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ChargeResponse(null, "ERROR", req.idempotencyKey()));
        }

        String method = req.paymentMethod();

        // async path -> PIX / BOLETO
        if ("PIX".equals(method) || "BOLETO".equals(method)) {
            String chargeId = UUID.randomUUID().toString();
            ChargeResponse pending = new ChargeResponse(chargeId, "PENDING", req.idempotencyKey());
            if (req.idempotencyKey() != null) store.put(req.idempotencyKey(), pending);
            scheduleWebhook(req, chargeId);
            log.info("{} charge {} PENDING for order {} — webhook in {}ms",
                    method, chargeId, req.orderId(), config.webhookDelayMs);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(pending);   // 202
        }

        // sync path -> CREDIT_CARD
        if (Math.random() < config.declineRate) {
            ChargeResponse declined = new ChargeResponse(
                    UUID.randomUUID().toString(), "DECLINED", req.idempotencyKey());
            if (req.idempotencyKey() != null) store.put(req.idempotencyKey(), declined);
            log.warn("Card declined for order {}", req.orderId());
            return ResponseEntity.ok(declined);
        }

        ChargeResponse approved = new ChargeResponse(
                UUID.randomUUID().toString(), "APPROVED", req.idempotencyKey());
        if (req.idempotencyKey() != null) store.put(req.idempotencyKey(), approved);
        log.info("Card approved for order {}", req.orderId());
        return ResponseEntity.ok(approved);
    }

    // the gateway actively calls back the order-service after a delay
    private void scheduleWebhook(ChargeRequest req, String chargeId) {
        scheduler.schedule(() -> {
            String status = (Math.random() < config.confirmRate) ? "CONFIRMED" : "EXPIRED";
            WebhookPayload payload = new WebhookPayload(
                    chargeId, req.orderId(), status, req.idempotencyKey());
            try {
                restTemplate.postForEntity(
                        config.orderServiceUrl + "/webhooks/payment-callback", payload, Void.class);
                log.info("Webhook delivered for order {} → {}", req.orderId(), status);
            } catch (Exception e) {
                log.error("Webhook delivery failed for order {}: {}", req.orderId(), e.getMessage());
            }
        }, config.webhookDelayMs, TimeUnit.MILLISECONDS);
    }
}