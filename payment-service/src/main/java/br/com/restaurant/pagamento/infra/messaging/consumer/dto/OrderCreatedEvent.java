package br.com.restaurant.pagamento.infra.messaging.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for the pedido.criado event published by pedido-service.
 * Schema confirmed and aligned with pedido-service.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreatedEvent {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("pedidoId")
    private String orderId;

    @JsonProperty("clienteId")
    private String customerId;

    @JsonProperty("restauranteId")
    private String restaurantId;

    @JsonProperty("valorTotal")
    private BigDecimal totalAmount;

    @JsonProperty("itens")
    private List<OrderItemDto> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItemDto {

        @JsonProperty("produtoId")
        private String productId;

        @JsonProperty("nome")
        private String name;

        @JsonProperty("quantidade")
        private Integer quantity;

        @JsonProperty("precoUnitario")
        private BigDecimal unitPrice;
    }
}
