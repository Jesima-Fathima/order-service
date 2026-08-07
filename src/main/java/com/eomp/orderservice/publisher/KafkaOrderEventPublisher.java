package com.eomp.orderservice.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.eomp.orderservice.event.OrderCreatedEvent;

/**
 * Kafka-backed implementation of the order event publisher.
 * It keeps the service layer decoupled from the Kafka template.
 */
@Component
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);

    @Value("${spring.kafka.topics.order-events:order-events}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        try {
            kafkaTemplate.send(topic, event.orderId().toString(), event);
            log.info("Publishing ORDER_CREATED event");
            log.info("Order Id: {}", event.orderId());
            log.info("Topic: {}", topic);
        } catch (Exception ex) {
            log.error("Failed to publish ORDER_CREATED event for orderId {} to topic {}", event.orderId(), topic, ex);
        }
    }
}
