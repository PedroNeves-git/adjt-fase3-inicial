package br.com.restaurant.pagamento.infra.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcpagResponse {
    private String status;       // ex: "APROVADO"
    private String transacaoId;  // ID da transação no procpag
}
