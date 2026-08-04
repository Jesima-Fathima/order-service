package com.eomp.orderservice.integration;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eomp.orderservice.api.v1.dto.OrderRequest;
import com.eomp.orderservice.entity.Order;
import com.eomp.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void createAndRetrieveOrder_endToEnd_success() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerName("Integration User");
        request.setProductCode("SKU-501");
        request.setQuantity(5);
        request.setTotalPrice(BigDecimal.valueOf(250.00));

        String created = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(created).contains("Integration User");

        Order savedOrder = orderRepository.findAll().get(0);
        assertThat(savedOrder.getProductCode()).isEqualTo("SKU-501");

        mockMvc.perform(get("/api/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.data.customerName").value("Integration User"));
    }

    @Test
    void getOrders_whenOrdersExist_returnsPagedResponse() throws Exception {
        Order order1 = new Order("A", "SKU-601", 1, BigDecimal.TEN, null);
        Order order2 = new Order("B", "SKU-602", 2, BigDecimal.valueOf(20), null);
        orderRepository.save(order1);
        orderRepository.save(order2);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }
}
