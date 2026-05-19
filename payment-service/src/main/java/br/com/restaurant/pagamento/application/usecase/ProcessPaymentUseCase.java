package br.com.restaurant.pagamento.application.usecase;

import br.com.restaurant.pagamento.application.port.in.ProcessPaymentInputPort;
import br.com.restaurant.pagamento.application.port.out.ExternalPaymentProcessorPort;
import br.com.restaurant.pagamento.application.port.out.PaymentEventPort;
import br.com.restaurant.pagamento.domain.exception.PaymentProcessingException;
import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentUseCase implements ProcessPaymentInputPort {

    private final ExternalPaymentProcessorPort externalProcessor;
    private final PaymentEventPort eventPort;

    @Override
    public void execute(String orderId, BigDecimal totalAmount) {
        log.info("[ProcessPaymentUseCase] Processing payment. orderId={}", orderId);

        // No try/catch intentionally.
        // If process() returns failed or throws, the Kafka consumer propagates the exception
        // and the error handler triggers retry with exponential backoff.
        // After retries are exhausted the message is sent to the DLT.
        ExternalPaymentResult result = externalProcessor.process(orderId, totalAmount);

        if (result.isApproved()) {
            log.info("[ProcessPaymentUseCase] Payment approved. orderId={}, transactionId={}",
                    orderId, result.getTransactionId());
            eventPort.publishApproved(orderId, result.getTransactionId(), totalAmount);
        } else {
            log.warn("[ProcessPaymentUseCase] Payment failed, triggering Kafka retry. orderId={}, reason={}",
                    orderId, result.getFailureReason());
            throw new PaymentProcessingException(orderId, result.getFailureReason());
        }
    }
}
