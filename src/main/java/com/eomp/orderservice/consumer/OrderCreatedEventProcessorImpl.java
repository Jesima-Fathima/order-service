package com.eomp.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.eomp.orderservice.event.OrderCreatedEvent;

/**
 * Default processor for order-created events.
 * For this milestone it logs the event and performs no database work.
 */
@Service
public class OrderCreatedEventProcessorImpl implements OrderCreatedEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventProcessorImpl.class);

    @Override
    public void process(OrderCreatedEvent event) {
        log.info("Processing ORDER_CREATED event for orderId {}", event.orderId());
    }
}
