package com.eomp.orderservice.publisher;

import com.eomp.orderservice.event.OrderCreatedEvent;

/**
 * Abstraction for publishing order domain events to the messaging layer.
 */
public interface OrderEventPublisher {

    void publish(OrderCreatedEvent event);
}
