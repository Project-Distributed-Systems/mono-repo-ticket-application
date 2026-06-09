package com.tickets.order_service.order;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    // (a) nested record; referenced by the controller as PaymentService.PaymentResult
    public record PaymentResult(String orderStatus, String message) {}

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Counter ticketsSoldCounter;

    @Value("${payment-gateway.url}")
    private String gatewayUrl;

    public PaymentService(RestTemplate restTemplate,
                        OrderRepository orderRepository,
                        RabbitTemplate rabbitTemplate,
                        MeterRegistry meterRegistry) {
        this.restTemplate = restTemplate;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.ticketsSoldCounter = Counter.builder("tickets_sold_total")
                .description("Total tickets successfully sold")
                .register(meterRegistry);
    }

    // (b) returns PaymentResult now, not void
    @CircuitBreaker(name = "gateway", fallbackMethod = "chargeFallback")
    @Retry(name = "gateway")
    public PaymentResult processPayment(Long orderId, String method, BigDecimal amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // idempotency guard; already confirmed orders are a no op
        if (order.getStatus() == Order.Status.CONFIRMED) {
            log.info("Order {} already confirmed; skipping", orderId);
            return new PaymentResult("CONFIRMED", "Pagamento já confirmado.");
        }

        order.setPaymentMethod(method);
        orderRepository.save(order);

        String idempotencyKey = "order-" + orderId;
        Map<String, Object> request = Map.of(
            "idempotencyKey", idempotencyKey,
            "paymentMethod", method,
            "orderId", orderId,
            "amount", amount
        );

        var response = restTemplate.postForEntity(gatewayUrl + "/charges", request, Map.class);
        String status = (String) response.getBody().get("status");

        switch (status) {
            case "PENDING" -> {
                String msg = "PIX".equals(method)
                    ? "PIX gerado; aguardando confirmação de pagamento."
                    : "Boleto gerado; aguardando confirmação de pagamento.";
                log.info("Order {} payment PENDING ({}); awaiting webhook", orderId, method);
                return new PaymentResult("PENDING", msg);
            }
            case "APPROVED" -> {
                confirmOrder(order);
                return new PaymentResult("CONFIRMED", "Pagamento aprovado.");
            }
            case "DECLINED" -> {
                order.setStatus(Order.Status.FAILED);
                orderRepository.save(order);
                throw new PaymentDeclinedException(orderId);
            }
            default -> throw new RuntimeException("Unexpected gateway status: " + status);
        }
    }

    // (c) fallback return type must match processPayment
    public PaymentResult chargeFallback(Long orderId, String method, BigDecimal amount, Exception ex) {
        log.error("Circuit breaker OPEN; fallback for order {}. Cause: {}", orderId, ex.getMessage());
        throw new RuntimeException("Payment service unavailable. Try again later.");
    }

    @Transactional
    public void handleWebhook(WebhookPayload payload) {
        Order order = orderRepository.findById(payload.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + payload.orderId()));

        if (order.getStatus() == Order.Status.CONFIRMED) {
            log.info("Order {} already confirmed; webhook idempotent skip", payload.orderId());
            return;
        }

        if ("CONFIRMED".equals(payload.status())) {
            confirmOrder(order);
        } else if ("EXPIRED".equals(payload.status())) {
            order.setStatus(Order.Status.EXPIRED);
            orderRepository.save(order);
            log.warn("Order {} payment EXPIRED ({})", payload.orderId(), order.getPaymentMethod());
        }
    }

    private void confirmOrder(Order order) {
        order.setStatus(Order.Status.CONFIRMED);
        orderRepository.save(order);

        OrderConfirmedEvent event = new OrderConfirmedEvent(
            order.getId(), order.getUserId(), order.getEventId());
        rabbitTemplate.convertAndSend(
            RabbitConfig.EXCHANGE, RabbitConfig.ORDER_CONFIRMED_ROUTING_KEY, event);

        ticketsSoldCounter.increment();
        log.info("Order {} confirmed and order.confirmed published", order.getId());
    }
}