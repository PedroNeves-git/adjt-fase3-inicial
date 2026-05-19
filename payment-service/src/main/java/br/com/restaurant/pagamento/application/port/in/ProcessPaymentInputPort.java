package br.com.restaurant.pagamento.application.port.in;

import java.math.BigDecimal;

public interface ProcessPaymentInputPort {

    void execute(String orderId, BigDecimal totalAmount);
}
