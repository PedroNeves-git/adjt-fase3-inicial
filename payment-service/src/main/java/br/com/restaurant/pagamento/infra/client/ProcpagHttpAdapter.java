package br.com.restaurant.pagamento.infra.client;

import br.com.restaurant.pagamento.application.port.out.ExternalPaymentProcessorPort;
import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;
import br.com.restaurant.pagamento.domain.model.FailureReason;
import br.com.restaurant.pagamento.infra.client.dto.ProcpagRequest;
import br.com.restaurant.pagamento.infra.client.dto.ProcpagResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

/**
 * Driven adapter — implements ExternalPaymentProcessorPort.
 * Fallback only classifies the failure and returns — no side effects.
 * The use case and DLT consumer decide what to do with the result.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcpagHttpAdapter implements ExternalPaymentProcessorPort {

    private final RestTemplate restTemplate;

    @Value("${procpag.url:http://procpag:8089}")
    private String procpagUrl;

    private static final String PAYMENTS_PATH = "/requisicao";

    @Override
    @CircuitBreaker(name = "procpag", fallbackMethod = "fallback")
    @Retry(name = "procpag")
    public ExternalPaymentResult process(String orderId, BigDecimal totalAmount) {
        log.info("[ProcpagHttpAdapter] Calling procpag. orderId={}", orderId);
        ProcpagRequest request = new ProcpagRequest(orderId, totalAmount.longValue(), orderId);
        restTemplate.postForObject(procpagUrl + PAYMENTS_PATH, request, String.class);

        ProcpagResponse response = restTemplate.getForObject(
                procpagUrl + PAYMENTS_PATH + "/" + orderId, ProcpagResponse.class);

        if (response != null && "pago".equalsIgnoreCase(response.getStatus())) {
            log.info("[ProcpagHttpAdapter] Approved. orderId={}, transactionId={}",
                    orderId, response.getPagamentoId());
            return ExternalPaymentResult.approved(response.getPagamentoId());
        }

        log.warn("[ProcpagHttpAdapter] Non-approved response. orderId={}", orderId);
        return ExternalPaymentResult.failed(FailureReason.ERRO_EXTERNO);
    }

    public ExternalPaymentResult fallback(String orderId, BigDecimal totalAmount, Throwable ex) {
        FailureReason reason = FailureReason.fromThrowable(ex);
        log.warn("[ProcpagHttpAdapter] Fallback. orderId={}, reason={}", orderId, reason);
        return ExternalPaymentResult.failed(reason);
    }
}
