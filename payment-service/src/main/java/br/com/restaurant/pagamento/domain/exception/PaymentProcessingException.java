package br.com.restaurant.pagamento.domain.exception;

import br.com.restaurant.pagamento.domain.model.FailureReason;

public class PaymentProcessingException extends RuntimeException {

    private final String orderId;
    private final FailureReason reason;

    public PaymentProcessingException(String orderId, FailureReason reason) {
        super("Payment processing failed for order " + orderId + ". Reason: " + reason);
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getOrderId()       { return orderId; }
    public FailureReason getReason() { return reason; }
}