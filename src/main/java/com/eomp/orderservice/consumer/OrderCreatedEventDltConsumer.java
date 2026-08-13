package com.eomp.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.eomp.orderservice.event.OrderCreatedEvent;

@Component
public class OrderCreatedEventDltConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventDltConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topics.order-events:order-events}.DLT",
            groupId = "order-service-dlt-consumer",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderCreatedEvent event) {

        log.error(
                "OrderCreatedEvent moved to DLT. orderId={}",
                event.orderId()
        );
    }
}