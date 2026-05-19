package br.com.restaurant.pagamento.application.port.in;

import java.math.BigDecimal;

public interface HandleDltPaymentInputPort {
    void execute(String orderId, BigDecimal totalAmount);
}