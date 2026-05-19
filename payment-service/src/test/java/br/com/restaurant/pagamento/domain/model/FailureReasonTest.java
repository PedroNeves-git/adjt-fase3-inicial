package br.com.restaurant.pagamento.domain.model;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FailureReason — unit tests")
class FailureReasonTest {

    @Test
    @DisplayName("null throwable should map to INDISPONIVEL")
    void nullThrowable_shouldMapToIndisponivel() {
        assertThat(FailureReason.fromThrowable(null)).isEqualTo(FailureReason.INDISPONIVEL);
    }

    @Test
    @DisplayName("TimeoutException should map to TIMEOUT")
    void timeoutException_shouldMapToTimeout() {
        assertThat(FailureReason.fromThrowable(new TimeoutException("timed out")))
                .isEqualTo(FailureReason.TIMEOUT);
    }

    @Test
    @DisplayName("CallNotPermittedException (circuit open) should map to CIRCUIT_OPEN")
    void callNotPermittedException_shouldMapToCircuitOpen() {
        CallNotPermittedException ex = CallNotPermittedException.createCallNotPermittedException(
                io.github.resilience4j.circuitbreaker.CircuitBreaker.ofDefaults("test"));
        assertThat(FailureReason.fromThrowable(ex)).isEqualTo(FailureReason.CIRCUIT_OPEN);
    }

    @Test
    @DisplayName("IOException should map to ERRO_EXTERNO")
    void ioException_shouldMapToErroExterno() {
        assertThat(FailureReason.fromThrowable(new IOException("connection refused")))
                .isEqualTo(FailureReason.ERRO_EXTERNO);
    }

    @Test
    @DisplayName("generic RuntimeException should map to ERRO_EXTERNO")
    void runtimeException_shouldMapToErroExterno() {
        assertThat(FailureReason.fromThrowable(new RuntimeException("unexpected")))
                .isEqualTo(FailureReason.ERRO_EXTERNO);
    }
}
