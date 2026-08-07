package com.eomp.orderservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable event payload published after a new order has been successfully persisted.
 * This keeps the public event contract free from JPA entity details.
 */
public record OrderCreatedEvent(
        Long orderId,
        String customerName,
        String productName,
        Integer quantity,
        BigDecimal totalPrice,
        String orderStatus,
        LocalDateTime createdAt
) {
}
