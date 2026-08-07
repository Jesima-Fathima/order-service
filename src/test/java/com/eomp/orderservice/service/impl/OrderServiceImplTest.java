package com.eomp.orderservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.eomp.orderservice.api.v1.dto.OrderRequest;
import com.eomp.orderservice.api.v1.dto.OrderResponse;
import com.eomp.orderservice.api.v1.dto.PageResponse;
import com.eomp.orderservice.entity.Order;
import com.eomp.orderservice.event.OrderCreatedEvent;
import com.eomp.orderservice.exception.ResourceNotFoundException;
import com.eomp.orderservice.publisher.OrderEventPublisher;
import com.eomp.orderservice.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest validRequest;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        validRequest = new OrderRequest();
        validRequest.setCustomerName("Jane Doe");
        validRequest.setProductCode("SKU-123");
        validRequest.setQuantity(2);
        validRequest.setTotalPrice(BigDecimal.valueOf(49.99));

        savedOrder = new Order(
                validRequest.getCustomerName(),
                validRequest.getProductCode(),
                validRequest.getQuantity(),
                validRequest.getTotalPrice(),
                LocalDateTime.now()
        );
        savedOrder.setId(1L);
    }

    @Test
    void createOrder_validRequest_returnsResponse() {
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);

        OrderResponse response = orderService.createOrder(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Jane Doe");
        assertThat(response.getProductCode()).isEqualTo("SKU-123");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(49.99));

        then(orderRepository).should(times(1)).save(any(Order.class));
        then(orderEventPublisher).should(times(1)).publish(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_publisherFailure_doesNotThrowException() {
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);
        doThrow(new RuntimeException("Kafka unavailable")).when(orderEventPublisher).publish(any(OrderCreatedEvent.class));

        OrderResponse response = orderService.createOrder(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        then(orderRepository).should(times(1)).save(any(Order.class));
        then(orderEventPublisher).should(times(1)).publish(any(OrderCreatedEvent.class));
    }

    @Test
    void getOrderById_existingOrder_returnsResponse() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(savedOrder));

        OrderResponse response = orderService.getOrderById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Jane Doe");
        then(orderRepository).should(times(1)).findById(1L);
    }

    @Test
    void getOrderById_missingOrder_throwsResourceNotFoundException() {
        given(orderRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id: 1");

        then(orderRepository).should(times(1)).findById(1L);
    }

    @Test
    void getOrders_pageableRequest_returnsPageResponse() {
        Order order2 = new Order("John Smith", "SKU-456", 1, BigDecimal.valueOf(19.99), LocalDateTime.now());
        order2.setId(2L);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Order> page = new PageImpl<>(List.of(savedOrder, order2), pageable, 2);
        given(orderRepository.findAll(pageable)).willReturn(page);

        PageResponse<OrderResponse> response = orderService.getOrders(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isLast()).isTrue();

        then(orderRepository).should(times(1)).findAll(pageable);
    }

    @Test
    void updateOrder_existingOrder_returnsUpdatedResponse() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(savedOrder));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerName("Jane Updated");
        updateRequest.setProductCode("SKU-789");
        updateRequest.setQuantity(3);
        updateRequest.setTotalPrice(BigDecimal.valueOf(79.99));

        OrderResponse response = orderService.updateOrder(1L, updateRequest);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Jane Updated");
        assertThat(response.getProductCode()).isEqualTo("SKU-789");
        assertThat(response.getQuantity()).isEqualTo(3);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(79.99));

        then(orderRepository).should(times(1)).findById(1L);
        then(orderRepository).should(times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_missingOrder_throwsResourceNotFoundException() {
        given(orderRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(1L, validRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        then(orderRepository).should(times(1)).findById(1L);
        then(orderRepository).should(times(0)).save(any(Order.class));
    }

    @Test
    void deleteOrder_existingOrder_deletesOrder() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(savedOrder));

        orderService.deleteOrder(1L);

        then(orderRepository).should(times(1)).findById(1L);
        then(orderRepository).should(times(1)).delete(savedOrder);
    }

    @Test
    void deleteOrder_missingOrder_throwsResourceNotFoundException() {
        given(orderRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteOrder(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        then(orderRepository).should(times(1)).findById(1L);
        then(orderRepository).should(times(0)).delete(any(Order.class));
    }

    @Test
    void createOrder_repositoryFailure_throwsException() {
        given(orderRepository.save(any(Order.class))).willThrow(new RuntimeException("database unavailable"));

        assertThatThrownBy(() -> orderService.createOrder(validRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("database unavailable");

        then(orderRepository).should(times(1)).save(any(Order.class));
    }
}
