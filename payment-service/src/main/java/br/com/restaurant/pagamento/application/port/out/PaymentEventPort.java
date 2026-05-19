package br.com.restaurant.pagamento.application.port.out;

import br.com.restaurant.pagamento.domain.model.FailureReason;

import java.math.BigDecimal;

public interface PaymentEventPort {

    void publishApproved(String orderId, String transactionId, BigDecimal amountPaid);

    void publishPending(String orderId, BigDecimal totalAmount, FailureReason reason, int attempt);
}
