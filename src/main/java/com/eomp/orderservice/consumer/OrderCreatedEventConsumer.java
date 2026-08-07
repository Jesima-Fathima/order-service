package com.eomp.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.eomp.orderservice.event.OrderCreatedEvent;

/**
 * Consumes ORDER_CREATED events from Kafka and logs them.
 * The consumer acknowledges the message only after successful processing.
 */
@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final OrderCreatedEventProcessor processor;

    public OrderCreatedEventConsumer(OrderCreatedEventProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.order-events:order-events}",
            groupId = "${spring.kafka.consumer.group-id:order-service-consumer}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received ORDER_CREATED event: {}", event);

        try {
            processor.process(event);
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process ORDER_CREATED event for orderId {}", event.orderId(), ex);
        }
    }
}
