package com.eomp.orderservice.api.v1;

import com.eomp.orderservice.api.v1.dto.ApiResponse;
import com.eomp.orderservice.api.v1.dto.OrderRequest;
import com.eomp.orderservice.api.v1.dto.OrderResponse;
import com.eomp.orderservice.api.v1.dto.PageResponse;
import com.eomp.orderservice.exception.GlobalExceptionHandler;
import com.eomp.orderservice.exception.ResourceNotFoundException;
import com.eomp.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderController.class)
@ContextConfiguration(classes = {OrderController.class, GlobalExceptionHandler.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest();
        orderRequest.setCustomerName("Alice");
        orderRequest.setProductCode("SKU-101");
        orderRequest.setQuantity(4);
        orderRequest.setTotalPrice(BigDecimal.valueOf(100.00));

        orderResponse = new OrderResponse(1L, "Alice", "SKU-101", 4, BigDecimal.valueOf(100.00), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void createOrder_validRequest_returnsCreated() throws Exception {
        given(orderService.createOrder(any(OrderRequest.class))).willReturn(orderResponse);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Order created successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.customerName", is("Alice")));

        then(orderService).should(times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void createOrder_missingCustomerName_returnsBadRequest() throws Exception {
        orderRequest.setCustomerName("");

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("customerName")));

        Mockito.verifyNoInteractions(orderService);
    }

    @Test
    void getOrderById_existingOrder_returnsOk() throws Exception {
        given(orderService.getOrderById(1L)).willReturn(orderResponse);

        mockMvc.perform(get("/api/v1/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.productCode", is("SKU-101")));

        then(orderService).should(times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_missingOrder_returnsNotFound() throws Exception {
        given(orderService.getOrderById(1L)).willThrow(new ResourceNotFoundException("Order not found with id: 1"));

        mockMvc.perform(get("/api/v1/orders/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Order not found with id: 1")));

        then(orderService).should(times(1)).getOrderById(1L);
    }

    @Test
    void getOrders_withPagingAndSorting_returnsOk() throws Exception {
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(
                List.of(orderResponse),
                0,
                10,
                1,
                1,
                true
        );

        given(orderService.getOrders(any(PageRequest.class))).willReturn(pageResponse);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page", is(0)))
                .andExpect(jsonPath("$.data.size", is(10)))
                .andExpect(jsonPath("$.data.totalElements", is(1)))
                .andExpect(jsonPath("$.data.content[0].customerName", is("Alice")));

        then(orderService).should(times(1)).getOrders(any(PageRequest.class));
    }

    @Test
    void updateOrder_existingOrder_returnsOk() throws Exception {
        given(orderService.updateOrder(eq(1L), any(OrderRequest.class))).willReturn(orderResponse);

        mockMvc.perform(put("/api/v1/orders/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.customerName", is("Alice")));

        then(orderService).should(times(1)).updateOrder(eq(1L), any(OrderRequest.class));
    }

    @Test
    void deleteOrder_existingOrder_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        then(orderService).should(times(1)).deleteOrder(1L);
    }
}
