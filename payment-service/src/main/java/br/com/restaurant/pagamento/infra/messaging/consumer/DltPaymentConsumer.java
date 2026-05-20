package br.com.restaurant.pagamento.infra.messaging.consumer;

import br.com.restaurant.pagamento.application.port.in.HandleDltPaymentInputPort;
import br.com.restaurant.pagamento.infra.messaging.consumer.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes messages from the Dead Letter Topic (pedido.criado-dlt).
 * Messages arrive here after all retries in the main consumer are exhausted.
 * Strategy: one attempt. If it fails, publishes pagamento.pendente and stops.
 * NEVER rethrows — prevents the message from looping on the DLT.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DltPaymentConsumer {

    private final HandleDltPaymentInputPort handleDltPayment;

    @KafkaListener(
            topics = "pedido.criado.DLT",
            groupId = "pagamento-service-dlt",
            containerFactory = "dltListenerContainerFactory"
    )
    public void onDlt(OrderCreatedEvent event) {
        log.warn("[DltPaymentConsumer] DLT message received. orderId={}", event.getOrderId());
        // HandleDltPaymentUseCase handles all exceptions internally and never rethrows,
        // so this message will not loop on the DLT.
        handleDltPayment.execute(event.getOrderId(), event.getTotalAmount());
    }
}
