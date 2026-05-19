package br.com.restaurant.pagamento.infra.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProcpagRequest {
    private String pedidoId;
    private BigDecimal valor;
}
