package br.com.restaurant.pagamento.domain.model;

public enum FailureReason {
    TIMEOUT,
    CIRCUIT_OPEN,
    ERRO_EXTERNO,
    INDISPONIVEL;

    public static FailureReason fromThrowable(Throwable ex) {
        if (ex == null) return INDISPONIVEL;
        String name = ex.getClass().getSimpleName().toLowerCase();
        if (name.contains("timeout") || name.contains("timelimiter")) return TIMEOUT;
        if (name.contains("callnotpermitted") || name.contains("circuitbreaker")) return CIRCUIT_OPEN;
        return ERRO_EXTERNO;
    }
}
