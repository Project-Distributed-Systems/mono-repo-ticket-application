package com.tickets.order_service.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusAndExpiresAtBefore(Order.Status status, LocalDateTime threshold);
}