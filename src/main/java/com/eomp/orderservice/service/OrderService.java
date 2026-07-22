package com.eomp.orderservice.service;

import com.eomp.orderservice.api.v1.dto.OrderRequest;
import com.eomp.orderservice.api.v1.dto.OrderResponse;
import com.eomp.orderservice.api.v1.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);

    PageResponse<OrderResponse> getOrders(Pageable pageable);

    OrderResponse updateOrder(Long id, OrderRequest request);

    void deleteOrder(Long id);
}
