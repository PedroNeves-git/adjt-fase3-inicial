package br.com.restaurant.pagamento.infra.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcpagResponse {
    @JsonProperty("pagamento_id")
    private String pagamentoId;
    private String status;
}
