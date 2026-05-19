package br.com.restaurant.pagamento.application.usecase;

import br.com.restaurant.pagamento.application.port.out.ExternalPaymentProcessorPort;
import br.com.restaurant.pagamento.application.port.out.PaymentEventPort;
import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;
import br.com.restaurant.pagamento.domain.model.FailureReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HandleDltPaymentUseCase — unit tests")
class HandleDltPaymentUseCaseTest {

    @Mock
    private ExternalPaymentProcessorPort externalProcessor;

    @Mock
    private PaymentEventPort eventPort;

    private HandleDltPaymentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new HandleDltPaymentUseCase(externalProcessor, eventPort);
    }

    @Test
    @DisplayName("DLT with late approval should publish pagamento.aprovado and not throw")
    void dltWithApproval_shouldPublishApproved() {
        when(externalProcessor.process(anyString(), any(BigDecimal.class)))
                .thenReturn(ExternalPaymentResult.approved("txn-dlt-456"));

        assertThatNoException().isThrownBy(
                () -> useCase.execute("order-dlt-1", new BigDecimal("100.00")));

        verify(eventPort).publishApproved("order-dlt-1", "txn-dlt-456", new BigDecimal("100.00"));
        verify(eventPort, never()).publishPending(any(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("DLT with definitive failure should publish pagamento.pendente with attempt=-1 and not throw")
    void dltWithFailure_shouldPublishPendingAndNotThrow() {
        when(externalProcessor.process(anyString(), any(BigDecimal.class)))
                .thenReturn(ExternalPaymentResult.failed(FailureReason.CIRCUIT_OPEN));

        assertThatNoException().isThrownBy(
                () -> useCase.execute("order-dlt-2", new BigDecimal("200.00")));

        verify(eventPort).publishPending(
                eq("order-dlt-2"), eq(new BigDecimal("200.00")), eq(FailureReason.CIRCUIT_OPEN), eq(-1));
        verify(eventPort, never()).publishApproved(any(), any(), any());
    }

    @Test
    @DisplayName("DLT with unexpected exception should publish pagamento.pendente and not throw — avoids infinite DLT loop")
    void dltWithException_shouldPublishPendingAndNotThrow() {
        when(externalProcessor.process(anyString(), any(BigDecimal.class)))
                .thenThrow(new RuntimeException("Unexpected failure"));

        assertThatNoException().isThrownBy(
                () -> useCase.execute("order-dlt-3", new BigDecimal("300.00")));

        verify(eventPort).publishPending(
                eq("order-dlt-3"), eq(new BigDecimal("300.00")), eq(FailureReason.ERRO_EXTERNO), eq(-1));
        verify(eventPort, never()).publishApproved(any(), any(), any());
    }
}
