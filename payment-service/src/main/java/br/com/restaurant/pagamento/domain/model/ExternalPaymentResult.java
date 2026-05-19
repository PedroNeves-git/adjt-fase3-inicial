package br.com.restaurant.pagamento.domain.model;

/**
 * Value Object representing the result of a payment processing attempt
 * at the external processor (procpag).
 */
public class ExternalPaymentResult {

    private final boolean approved;
    private final String transactionId;
    private final FailureReason failureReason; // null when approved = true

    private ExternalPaymentResult(boolean approved, String transactionId, FailureReason failureReason) {
        this.approved = approved;
        this.transactionId = transactionId;
        this.failureReason = failureReason;
    }

    public static ExternalPaymentResult approved(String transactionId) {
        return new ExternalPaymentResult(true, transactionId, null);
    }

    public static ExternalPaymentResult failed(FailureReason reason) {
        return new ExternalPaymentResult(false, null, reason);
    }

    public boolean isApproved()             { return approved; }
    public String getTransactionId()        { return transactionId; }
    public FailureReason getFailureReason() { return failureReason; }
}
