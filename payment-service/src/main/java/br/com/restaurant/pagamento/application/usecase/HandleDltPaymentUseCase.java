package br.com.restaurant.pagamento.application.usecase;

import br.com.restaurant.pagamento.application.port.in.HandleDltPaymentInputPort;
import br.com.restaurant.pagamento.application.port.out.ExternalPaymentProcessorPort;
import br.com.restaurant.pagamento.application.port.out.PaymentEventPort;
import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;
import br.com.restaurant.pagamento.domain.model.FailureReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandleDltPaymentUseCase implements HandleDltPaymentInputPort {

    private final ExternalPaymentProcessorPort externalProcessor;
    private final PaymentEventPort eventPort;

    @Override
    public void execute(String orderId, BigDecimal totalAmount) {
        log.warn("[HandleDltPaymentUseCase] Processing DLT message. orderId={}", orderId);
        try {
            ExternalPaymentResult result = externalProcessor.process(orderId, totalAmount);
            if (result.isApproved()) {
                log.info("[HandleDltPaymentUseCase] Late approval. orderId={}", orderId);
                eventPort.publishApproved(orderId, result.getTransactionId(), totalAmount);
            } else {
                publishPendingAndGiveUp(orderId, totalAmount, result.getFailureReason());
            }
        } catch (Exception e) {
            log.error("[HandleDltPaymentUseCase] Exception on DLT attempt. orderId={}. Error: {}",
                    orderId, e.getMessage());
            publishPendingAndGiveUp(orderId, totalAmount, FailureReason.fromThrowable(e));
        }
    }

    private void publishPendingAndGiveUp(String orderId, BigDecimal totalAmount, FailureReason reason) {
        log.error("[HandleDltPaymentUseCase] Giving up. Publishing pagamento.pendente. orderId={}, reason={}",
                orderId, reason);
        // tentativa = -1 indicates message came from DLT (all retries exhausted)
        eventPort.publishPending(orderId, totalAmount, reason, -1);
    }
}
