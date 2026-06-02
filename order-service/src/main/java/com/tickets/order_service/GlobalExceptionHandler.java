package com.tickets.order_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tickets.order_service.order.GatewayUnavailableException;
import com.tickets.order_service.order.InsufficientInventoryException;
import com.tickets.order_service.order.InvalidOrderStateException;
import com.tickets.order_service.order.OrderNotFoundException;
import com.tickets.order_service.order.PaymentDeclinedException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientInventoryException.class)
    public ResponseEntity<Map<String, String>> handleInsufficient(InsufficientInventoryException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(PaymentDeclinedException.class)
public ResponseEntity<Map<String, String>> handleDeclined(PaymentDeclinedException ex) {
    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)  // 402
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidState(InvalidOrderStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)  // 409
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)  // 404
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(GatewayUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleGatewayDown(GatewayUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)  // 503
                .body(Map.of("error", "Payment service unavailable. Try again later."));
    }

}