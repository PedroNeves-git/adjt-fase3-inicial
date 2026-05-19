package br.com.restaurant.pagamento.domain.exception;

public class ProcpagUnavailableException extends RuntimeException {

    public ProcpagUnavailableException(String orderId, String reason) {
        super("Procpag unavailable for order " + orderId + ". Reason: " + reason);
    }
}
