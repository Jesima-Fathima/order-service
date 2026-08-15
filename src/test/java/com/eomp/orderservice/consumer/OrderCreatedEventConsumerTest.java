package com.eomp.orderservice.consumer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.springframework.kafka.support.Acknowledgment;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.eomp.orderservice.event.OrderCreatedEvent;
import com.eomp.orderservice.repository.ProcessedEventRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCreatedEventConsumerTest {

    @Test
    void consume_successfulProcessing_acknowledges() {
        OrderCreatedEventProcessor processor = mock(OrderCreatedEventProcessor.class);
        ProcessedEventRepository processedRepo = mock(ProcessedEventRepository.class);
        OrderCreatedEventConsumer consumer = new OrderCreatedEventConsumer(processor, processedRepo, new SimpleMeterRegistry());
        OrderCreatedEvent event = new OrderCreatedEvent(
            UUID.randomUUID(),
            1L,
            "Jane Doe",
            "SKU-123",
            Integer.valueOf(2),
            BigDecimal.valueOf(49.99),
            "CREATED",
            LocalDateTime.now()
        );
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        ConsumerRecord<String, OrderCreatedEvent> record =
        new ConsumerRecord<>(
                "order-events",
                0,
                10L,
                "order-key",
                event
        );

        consumer.consume(event, acknowledgment, record);
        

        verify(processor).process(event);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void consume_processingFailure_doesNotAcknowledge() {
        OrderCreatedEventProcessor processor = mock(OrderCreatedEventProcessor.class);
        ProcessedEventRepository processedRepo = mock(ProcessedEventRepository.class);

        OrderCreatedEventConsumer consumer =
            new OrderCreatedEventConsumer(
                    processor,
                    processedRepo,
                    new SimpleMeterRegistry());

        OrderCreatedEvent event = new OrderCreatedEvent(
            UUID.randomUUID(),
            2L,
            "John Doe",
            "SKU-456",
            Integer.valueOf(1),
            BigDecimal.valueOf(19.99),
            "CREATED",
            LocalDateTime.now()
        );

        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        doThrow(new RuntimeException("processing failed"))
            .when(processor)
            .process(event);

        ConsumerRecord<String, OrderCreatedEvent> record =
            new ConsumerRecord<>(
                    "order-events",
                    0,
                    10L,
                    "order-key",
                    event
            );

        assertThrows(
            RuntimeException.class,
            () -> consumer.consume(event, acknowledgment, record)
        );

        verify(processor).process(event);
        verify(acknowledgment, never()).acknowledge();
    }
}
