package com.restaurant.order_service.order.infra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.dto.input.CreateOrderInputDTO;
import com.restaurant.order_service.order.core.dto.output.OrderItemOutputDTO;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.exception.OrderAccessDeniedException;
import com.restaurant.order_service.order.core.exception.OrderNotFoundException;
import com.restaurant.order_service.order.core.usecase.ConfirmOrderUseCase;
import com.restaurant.order_service.order.core.usecase.CreateOrderUseCase;
import com.restaurant.order_service.order.core.usecase.GetOrderByIdUseCase;
import com.restaurant.order_service.order.core.usecase.ListClientOrdersUseCase;
import com.restaurant.order_service.order.infra.dto.CreateOrderItemRequest;
import com.restaurant.order_service.order.infra.dto.CreateOrderRequest;
import com.restaurant.order_service.security.AuthenticatedClient;
import com.restaurant.order_service.security.JwtTokenValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrderControllerTest {

    private static final Long CLIENT_ID = 42L;

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;

    @MockBean private CreateOrderUseCase createOrder;
    @MockBean private ConfirmOrderUseCase confirmOrder;
    @MockBean private GetOrderByIdUseCase getOrderById;
    @MockBean private ListClientOrdersUseCase listClientOrders;
    @MockBean private JwtTokenValidator jwtTokenValidator;

    @BeforeEach
    void authenticate() {
        AuthenticatedClient client = new AuthenticatedClient(CLIENT_ID, "test@example.com", "CLIENT");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        client, null, List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
                )
        );
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private OrderOutputDTO sampleOrder(UUID id, OrderStatus status) {
        OrderItemOutputDTO item = new OrderItemOutputDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "X-Burger",
                2,
                new BigDecimal("25.00"),
                new BigDecimal("50.00")
        );
        return new OrderOutputDTO(
                id, CLIENT_ID, UUID.randomUUID(),
                List.of(item),
                new BigDecimal("50.00"),
                status,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /orders returns 201 with the created order")
    void postOrdersCreatesOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        CreateOrderRequest req = new CreateOrderRequest(
                restaurantId,
                List.of(new CreateOrderItemRequest(menuItemId, 2))
        );

        when(createOrder.execute(any(CreateOrderInputDTO.class)))
                .thenReturn(sampleOrder(orderId, OrderStatus.AWAITING_CONFIRMATION));

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("AWAITING_CONFIRMATION"))
                .andExpect(jsonPath("$.totalAmount").value(50.00))
                .andExpect(jsonPath("$.items[0].name").value("X-Burger"));
    }

    @Test
    @DisplayName("POST /orders returns 400 when body fails validation")
    void postOrdersValidationError() throws Exception {
        String invalid = "{\"restaurantId\": null, \"items\": []}";

        mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("POST /orders/{id}/confirm returns 200 with PENDING_PAYMENT status")
    void confirmOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(confirmOrder.execute(eq(orderId), eq(CLIENT_ID)))
                .thenReturn(sampleOrder(orderId, OrderStatus.PENDING_PAYMENT));

        mvc.perform(post("/orders/{id}/confirm", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));

        verify(confirmOrder).execute(orderId, CLIENT_ID);
    }

    @Test
    @DisplayName("POST /orders/{id}/confirm returns 404 when order not found")
    void confirmNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(confirmOrder.execute(eq(orderId), eq(CLIENT_ID)))
                .thenThrow(new OrderNotFoundException(orderId));

        mvc.perform(post("/orders/{id}/confirm", orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("OrderNotFoundException"));
    }

    @Test
    @DisplayName("GET /orders/{id} returns 403 when order belongs to another client")
    void getByIdAccessDenied() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(getOrderById.execute(eq(orderId), eq(CLIENT_ID)))
                .thenThrow(new OrderAccessDeniedException());

        mvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("OrderAccessDenied"));
    }

    @Test
    @DisplayName("GET /orders lists the authenticated client's orders")
    void listOrders() throws Exception {
        when(listClientOrders.execute(CLIENT_ID))
                .thenReturn(List.of(
                        sampleOrder(UUID.randomUUID(), OrderStatus.PAID),
                        sampleOrder(UUID.randomUUID(), OrderStatus.PENDING_PAYMENT)
                ));

        mvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("PAID"))
                .andExpect(jsonPath("$[1].status").value("PENDING_PAYMENT"));
    }
}
