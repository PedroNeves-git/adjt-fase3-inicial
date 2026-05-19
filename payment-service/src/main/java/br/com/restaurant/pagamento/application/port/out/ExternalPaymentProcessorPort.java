package br.com.restaurant.pagamento.application.port.out;

import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;

import java.math.BigDecimal;

public interface ExternalPaymentProcessorPort {

    ExternalPaymentResult process(String orderId, BigDecimal totalAmount);
}
