package br.com.restaurant.pagamento.infra.messaging.producer.dto;

import br.com.restaurant.pagamento.domain.model.FailureReason;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Event published to the pagamento.pendente topic.
 * Schema aligned with pedido-service contract.
 * Consumed by pedido-service (status update) and own worker (reprocessing).
 * JSON field names kept in Portuguese to match the agreed contract.
 */
@Data
@Builder
public class PendingPaymentEvent {

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")   // fixed value: "PAGAMENTO_PENDENTE"
    private String eventType;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("pedidoId")
    private String pedidoId;

    @JsonProperty("valorTotal")
    private BigDecimal valorTotal;

    @JsonProperty("motivo")      // serializes as string: "TIMEOUT", "CIRCUIT_OPEN", etc.
    private FailureReason motivo;

    @JsonProperty("tentativa")
    private int tentativa;
}
