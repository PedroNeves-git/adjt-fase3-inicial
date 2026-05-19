package br.com.restaurant.pagamento.application.usecase;

import br.com.restaurant.pagamento.application.port.out.ExternalPaymentProcessorPort;
import br.com.restaurant.pagamento.application.port.out.PaymentEventPort;
import br.com.restaurant.pagamento.domain.exception.PaymentProcessingException;
import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;
import br.com.restaurant.pagamento.domain.model.FailureReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessPaymentUseCase — unit tests")
class ProcessPaymentUseCaseTest {

    @Mock
    private ExternalPaymentProcessorPort externalProcessor;

    @Mock
    private PaymentEventPort eventPort;

    private ProcessPaymentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcessPaymentUseCase(externalProcessor, eventPort);
    }

    @Test
    @DisplayName("when processor returns approved, should publish approved event and not throw")
    void whenProcessorReturnsApproved_shouldPublishApproved() {
        when(externalProcessor.process(anyString(), any(BigDecimal.class)))
                .thenReturn(ExternalPaymentResult.approved("txn-123"));

        useCase.execute("order-100", new BigDecimal("99.90"));

        verify(eventPort).publishApproved("order-100", "txn-123", new BigDecimal("99.90"));
    }

    @Test
    @DisplayName("when processor returns failed (fallback active), should throw PaymentProcessingException and not publish approved")
    void whenProcessorReturnsFailed_shouldThrowPaymentProcessingException() {
        when(externalProcessor.process(anyString(), any(BigDecimal.class)))
                .thenReturn(ExternalPaymentResult.failed(FailureReason.TIMEOUT));

        assertThatThrownBy(() -> useCase.execute("order-101", new BigDecimal("50.00")))
                .isInstanceOf(PaymentProcessingException.class);

        verify(eventPort, never()).publishApproved(any(), any(), any());
    }

    @Test
    @DisplayName("when processor throws exception, exception propagates so Kafka can retry")
    void whenProcessorThrowsException_exceptionPropagates() {
        when(externalProcessor.process(anyString(), any(BigDecimal.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        assertThatThrownBy(() -> useCase.execute("order-102", new BigDecimal("200.00")))
                .isInstanceOf(RuntimeException.class);

        verify(eventPort, never()).publishApproved(any(), any(), any());
    }
}
