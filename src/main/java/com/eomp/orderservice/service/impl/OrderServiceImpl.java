package com.eomp.orderservice.service.impl;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eomp.orderservice.api.v1.dto.OrderRequest;
import com.eomp.orderservice.api.v1.dto.OrderResponse;
import com.eomp.orderservice.api.v1.dto.PageResponse;
import com.eomp.orderservice.entity.Order;
import com.eomp.orderservice.event.OrderCreatedEvent;
import com.eomp.orderservice.exception.ResourceNotFoundException;
import com.eomp.orderservice.publisher.OrderEventPublisher;
import com.eomp.orderservice.repository.OrderRepository;
import com.eomp.orderservice.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order(
                request.getCustomerName(),
                request.getProductCode(),
                request.getQuantity(),
                request.getTotalPrice(),
                LocalDateTime.now()
        );

        Order savedOrder = orderRepository.save(order);
        publishOrderCreatedEvent(savedOrder);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return mapToResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);

        return new PageResponse<>(
                orderPage.stream().map(this::mapToResponse).collect(Collectors.toList()),
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = findOrderById(id);

        order.setCustomerName(request.getCustomerName());
        order.setProductCode(request.getProductCode());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = findOrderById(id);
        orderRepository.delete(order);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void publishOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerName(),
                order.getProductCode(),
                order.getQuantity(),
                order.getTotalPrice(),
                "CREATED",
                order.getCreatedAt()
        );

        try {
            orderEventPublisher.publish(event);
        } catch (Exception ex) {
            log.error("Failed to publish ORDER_CREATED event for orderId {}", order.getId(), ex);
        }
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getProductCode(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
