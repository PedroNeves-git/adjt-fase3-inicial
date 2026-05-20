package com.restaurant.order_service.order.infra.messaging.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentApprovedEvent(
        @JsonProperty("eventId")     UUID eventId,
        @JsonProperty("eventType")   String eventType,
        @JsonProperty("timestamp")   Instant timestamp,
        @JsonProperty("pedidoId")    UUID orderId,
        @JsonProperty("transacaoId") String transactionId,
        @JsonProperty("valorPago")   BigDecimal amountPaid
) {

    public static final String EVENT_TYPE = "PAGAMENTO_APROVADO";
}
