package com.eomp.orderservice.consumer;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.eomp.orderservice.entity.ProcessedEvent;
import com.eomp.orderservice.event.OrderCreatedEvent;
import com.eomp.orderservice.repository.ProcessedEventRepository;

/**
 * Consumes ORDER_CREATED events from Kafka and logs them.
 * The consumer acknowledges the message only after successful processing.
 */
@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final OrderCreatedEventProcessor processor;
    private final ProcessedEventRepository processedEventRepository;

    public OrderCreatedEventConsumer(
        OrderCreatedEventProcessor processor,
        ProcessedEventRepository processedEventRepository) {

        this.processor = processor;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(
        topics = "${spring.kafka.topics.order-events:order-events}",
        groupId = "${spring.kafka.consumer.group-id:order-service-consumer}",
        containerFactory = "kafkaListenerContainerFactory")
    public void consume(OrderCreatedEvent event,Acknowledgment acknowledgment) {

        log.info("Received ORDER_CREATED event. eventId={}, orderId={}",
            event.eventId(),
            event.orderId());

        if (processedEventRepository.existsByEventId(event.eventId())) {

            log.info("Event already processed. Skipping. eventId={}",
                event.eventId());

            acknowledgment.acknowledge();
            return;
        }

        processor.process(event);

        processedEventRepository.save(new ProcessedEvent(
                    event.eventId(),
                    "ORDER_CREATED",
                    LocalDateTime.now())
        );

        acknowledgment.acknowledge();
    }
    /* 
    public void consume(OrderCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received ORDER_CREATED event: {}", event);

        try {
            processor.process(event);
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process ORDER_CREATED event for orderId {}", event.orderId(), ex);
        }
    }*/
}
