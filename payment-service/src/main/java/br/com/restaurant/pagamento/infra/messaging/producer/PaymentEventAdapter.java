package br.com.restaurant.pagamento.infra.messaging.producer;

import br.com.restaurant.pagamento.application.port.out.PaymentEventPort;
import br.com.restaurant.pagamento.domain.model.FailureReason;
import br.com.restaurant.pagamento.infra.messaging.producer.dto.PaymentApprovedEvent;
import br.com.restaurant.pagamento.infra.messaging.producer.dto.PendingPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventAdapter implements PaymentEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.pagamento-aprovado}")
    private String approvedTopic;

    @Value("${kafka.topics.pagamento-pendente}")
    private String pendingTopic;

    @Override
    public void publishApproved(String orderId, String transactionId, BigDecimal amountPaid) {
        PaymentApprovedEvent event = PaymentApprovedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PAGAMENTO_APROVADO")
                .timestamp(Instant.now().toString())
                .pedidoId(orderId)
                .transacaoId(transactionId)
                .valorPago(amountPaid)
                .build();
        kafkaTemplate.send(approvedTopic, orderId, event); // orderId as key — ensures ordering
        log.info("[PaymentEventAdapter] pagamento.aprovado published. pedidoId={}, transacaoId={}",
                orderId, transactionId);
    }

    @Override
    public void publishPending(String orderId, BigDecimal totalAmount, FailureReason reason, int attempt) {
        PendingPaymentEvent event = PendingPaymentEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PAGAMENTO_PENDENTE")
                .timestamp(Instant.now().toString())
                .pedidoId(orderId)
                .valorTotal(totalAmount)
                .motivo(reason)
                .tentativa(attempt)
                .build();
        kafkaTemplate.send(pendingTopic, orderId, event);
        log.info("[PaymentEventAdapter] pagamento.pendente published. pedidoId={}, motivo={}, tentativa={}",
                orderId, reason, attempt);
    }
}
