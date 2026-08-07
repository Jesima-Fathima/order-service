package com.eomp.orderservice.consumer;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import com.eomp.orderservice.event.OrderCreatedEvent;

class OrderCreatedEventConsumerTest {

    @Test
    void consume_successfulProcessing_acknowledges() {
        OrderCreatedEventProcessor processor = mock(OrderCreatedEventProcessor.class);
        OrderCreatedEventConsumer consumer = new OrderCreatedEventConsumer(processor);
        OrderCreatedEvent event = new OrderCreatedEvent(
                1L,
                "Jane Doe",
                "SKU-123",
                2,
                BigDecimal.valueOf(49.99),
                "CREATED",
                LocalDateTime.now()
        );
        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        consumer.consume(event, acknowledgment);

        verify(processor).process(event);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void consume_processingFailure_doesNotAcknowledge() {
        OrderCreatedEventProcessor processor = mock(OrderCreatedEventProcessor.class);
        OrderCreatedEventConsumer consumer = new OrderCreatedEventConsumer(processor);
        OrderCreatedEvent event = new OrderCreatedEvent(
                2L,
                "John Doe",
                "SKU-456",
                1,
                BigDecimal.valueOf(19.99),
                "CREATED",
                LocalDateTime.now()
        );
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        doThrow(new RuntimeException("processing failed")).when(processor).process(event);

        consumer.consume(event, acknowledgment);

        verify(processor).process(event);
        verify(acknowledgment, never()).acknowledge();
    }
}
