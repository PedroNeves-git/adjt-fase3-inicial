package br.com.restaurant.pagamento.infra.messaging.producer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Event published to the pagamento.aprovado topic.
 * Schema aligned with pedido-service contract.
 * JSON field names kept in Portuguese to match the agreed contract.
 */
@Data
@Builder
public class PaymentApprovedEvent {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")   // fixed value: "PAGAMENTO_APROVADO"
    private String eventType;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("pedidoId")
    private String pedidoId;

    @JsonProperty("transacaoId")
    private String transacaoId;

    @JsonProperty("valorPago")
    private BigDecimal valorPago;
}
