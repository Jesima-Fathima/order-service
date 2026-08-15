package com.eomp.orderservice.consumer;

import java.time.LocalDateTime;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.eomp.orderservice.entity.ProcessedEvent;
import com.eomp.orderservice.event.OrderCreatedEvent;
import com.eomp.orderservice.repository.ProcessedEventRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Consumes ORDER_CREATED events from Kafka and logs them.
 * The consumer acknowledges the message only after successful processing.
 */
@Component
public class OrderCreatedEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final OrderCreatedEventProcessor processor;
    private final ProcessedEventRepository processedEventRepository;

    private final Counter processedEventsCounter;
    private final Counter duplicateEventsCounter;
    private final Timer eventProcessingTimer;

    public OrderCreatedEventConsumer(
            OrderCreatedEventProcessor processor,
            ProcessedEventRepository processedEventRepository,
            MeterRegistry meterRegistry) {

        this.processor = processor;
        this.processedEventRepository = processedEventRepository;

        this.processedEventsCounter = Counter.builder("order.events.processed")
                .description("Number of ORDER_CREATED events processed successfully")
                .register(meterRegistry);

        this.duplicateEventsCounter = Counter.builder("order.events.duplicates")
                .description("Number of duplicate ORDER_CREATED events skipped")
                .register(meterRegistry);

        this.eventProcessingTimer = Timer.builder("order.events.processing.time")
                .description("Time taken to process ORDER_CREATED events")
                .register(meterRegistry);
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.order-events:order-events}",
            groupId = "${spring.kafka.consumer.group-id:order-service-consumer}",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(
            OrderCreatedEvent event,
            Acknowledgment acknowledgment,
            ConsumerRecord<String, OrderCreatedEvent> record) {

        long startTime = System.nanoTime();

        log.info(
                "Received ORDER_CREATED event. topic={}, partition={}, offset={}, eventId={}, orderId={}",
                record.topic(),
                record.partition(),
                record.offset(),
                event.eventId(),
                event.orderId());

        // Idempotency check
        if (processedEventRepository.existsByEventId(event.eventId())) {

            duplicateEventsCounter.increment();

            long durationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            log.info(
                    "Event already processed. Skipping. topic={}, partition={}, offset={}, eventId={}, orderId={}, durationMs={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    event.eventId(),
                    event.orderId(),
                    durationMs);

            acknowledgment.acknowledge();
            return;
        }

        // Process event
        processor.process(event);

        // Store event ID for idempotency
        processedEventRepository.save(
                new ProcessedEvent(
                        event.eventId(),
                        "ORDER_CREATED",
                        LocalDateTime.now()));

        // Record successful processing
        processedEventsCounter.increment();

        long durationMs =
                (System.nanoTime() - startTime) / 1_000_000;

        eventProcessingTimer.record(
                System.nanoTime() - startTime,
                java.util.concurrent.TimeUnit.NANOSECONDS);

        // Acknowledge only after successful processing
        acknowledgment.acknowledge();

        log.info(
                "ORDER_CREATED event processed successfully. topic={}, partition={}, offset={}, eventId={}, orderId={}, durationMs={}",
                record.topic(),
                record.partition(),
                record.offset(),
                event.eventId(),
                event.orderId(),
                durationMs);
    }
}