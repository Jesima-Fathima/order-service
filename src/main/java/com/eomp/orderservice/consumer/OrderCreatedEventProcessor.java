package com.eomp.orderservice.consumer;

import com.eomp.orderservice.event.OrderCreatedEvent;

/**
 * Simple processing contract for order-created events.
 * The current milestone keeps the work focused on logging and handoff.
 */
public interface OrderCreatedEventProcessor {

    void process(OrderCreatedEvent event);
}
