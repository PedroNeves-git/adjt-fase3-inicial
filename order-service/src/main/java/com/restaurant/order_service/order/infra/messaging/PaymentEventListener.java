package com.restaurant.order_service.order.infra.messaging;

import com.restaurant.order_service.order.core.usecase.MarkOrderAsPaidUseCase;
import com.restaurant.order_service.order.infra.messaging.event.PaymentApprovedEvent;
import com.restaurant.order_service.order.infra.messaging.event.PaymentPendingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final MarkOrderAsPaidUseCase markOrderAsPaid;

    public PaymentEventListener(MarkOrderAsPaidUseCase markOrderAsPaid) {
        this.markOrderAsPaid = markOrderAsPaid;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.pagamento-aprovado}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.restaurant.order_service.order.infra.messaging.event.PaymentApprovedEvent"
            }
    )
    public void onPaymentApproved(PaymentApprovedEvent event) {
        log.info(
                "Received PAYMENT_APPROVED for order {} (transactionId={}, amountPaid={})",
                event.orderId(), event.transactionId(), event.amountPaid()
        );
        markOrderAsPaid.execute(event.orderId());
    }

    @KafkaListener(
            topics = "${app.kafka.topics.pagamento-pendente}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.restaurant.order_service.order.infra.messaging.event.PaymentPendingEvent"
            }
    )
    public void onPaymentPending(PaymentPendingEvent event) {
        log.warn(
                "Received PAYMENT_PENDING for order {} (reason={}, attempt={}). "
                        + "Order remains in PENDING_PAYMENT awaiting payment-service retry.",
                event.orderId(), event.reason(), event.attempt()
        );
    }
}
