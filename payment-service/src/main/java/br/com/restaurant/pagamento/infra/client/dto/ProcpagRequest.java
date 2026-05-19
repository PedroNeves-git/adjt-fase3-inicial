package br.com.restaurant.pagamento.infra.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcpagRequest {
    @JsonProperty("pagamento_id")
    private String pagamentoId;
    @JsonProperty("valor")
    private long valor;
    @JsonProperty("cliente_id")
    private String clienteId;
}
