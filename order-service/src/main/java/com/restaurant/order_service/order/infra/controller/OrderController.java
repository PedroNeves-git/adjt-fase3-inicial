package com.restaurant.order_service.order.infra.controller;

import com.restaurant.order_service.order.core.dto.input.CreateOrderInputDTO;
import com.restaurant.order_service.order.core.dto.input.CreateOrderItemInputDTO;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.usecase.ConfirmOrderUseCase;
import com.restaurant.order_service.order.core.usecase.CreateOrderUseCase;
import com.restaurant.order_service.order.core.usecase.GetOrderByIdUseCase;
import com.restaurant.order_service.order.core.usecase.ListClientOrdersUseCase;
import com.restaurant.order_service.order.infra.dto.CreateOrderRequest;
import com.restaurant.order_service.security.CurrentClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Create, confirm and query customer orders")
public class OrderController {

    private final CreateOrderUseCase createOrder;
    private final ConfirmOrderUseCase confirmOrder;
    private final GetOrderByIdUseCase getOrderById;
    private final ListClientOrdersUseCase listClientOrders;

    public OrderController(
            CreateOrderUseCase createOrder,
            ConfirmOrderUseCase confirmOrder,
            GetOrderByIdUseCase getOrderById,
            ListClientOrdersUseCase listClientOrders
    ) {
        this.createOrder = createOrder;
        this.confirmOrder = confirmOrder;
        this.getOrderById = getOrderById;
        this.listClientOrders = listClientOrders;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order in AWAITING_CONFIRMATION status",
               description = "Calculates total from catalog prices and returns the order for client confirmation.")
    public OrderOutputDTO create(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderInputDTO input = new CreateOrderInputDTO(
                CurrentClient.clientId(),
                request.restaurantId(),
                request.items().stream()
                        .map(it -> new CreateOrderItemInputDTO(it.menuItemId(), it.quantity()))
                        .toList()
        );
        return createOrder.execute(input);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm an order, transitioning it to PENDING_PAYMENT",
               description = "Publishes the 'pedido.criado' event so the payment-service can process it.")
    public OrderOutputDTO confirm(@PathVariable UUID id) {
        return confirmOrder.execute(id, CurrentClient.clientId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order owned by the authenticated client")
    public OrderOutputDTO getById(@PathVariable UUID id) {
        return getOrderById.execute(id, CurrentClient.clientId());
    }

    @GetMapping
    @Operation(summary = "List all orders for the authenticated client, newest first")
    public List<OrderOutputDTO> list() {
        return listClientOrders.execute(CurrentClient.clientId());
    }
}
