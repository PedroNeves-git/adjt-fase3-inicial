package br.com.restaurant.pagamento.infra.messaging.consumer;

import br.com.restaurant.pagamento.application.port.in.ProcessPaymentInputPort;
import br.com.restaurant.pagamento.infra.messaging.consumer.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Driving adapter: consumes pedido.criado event and triggers the payment processing use case.
 * Exceptions propagate to the Kafka error handler, which retries with exponential backoff
 * and sends to pedido.criado-dlt after all retries are exhausted.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProcessPaymentInputPort processPayment;

    @KafkaListener(
            topics = "${kafka.topics.pedido-criado}",
            groupId = "payment-service",
            containerFactory = "orderCreatedListenerContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[OrderCreatedConsumer] Event received. orderId={}, totalAmount={}",
                event.getOrderId(), event.getTotalAmount());
        processPayment.execute(event.getOrderId(), event.getTotalAmount());
    }
}
