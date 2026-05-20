package com.restaurant.order_service.order.infra.messaging;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import com.restaurant.order_service.order.infra.messaging.event.PaymentApprovedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(
        topics = {"pedido.criado", "pagamento.aprovado", "pagamento.pendente"},
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("test")
class PaymentEventListenerIntegrationTest {

    @Autowired private OrderGateway orderGateway;
    @Autowired private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.pagamento-aprovado}")
    private String paymentApprovedTopic;

    @Test
    @DisplayName("consumes PAYMENT_APPROVED and transitions order from PENDING_PAYMENT to PAID")
    void marksOrderAsPaidWhenEventArrives() {
        Order order = Order.newOrder(
                42L,
                UUID.randomUUID(),
                List.of(OrderItem.create(UUID.randomUUID(), "X-Burger", 2, new BigDecimal("25.00")))
        );
        order.confirm();
        Order saved = orderGateway.save(order);

        PaymentApprovedEvent event = new PaymentApprovedEvent(
                UUID.randomUUID(),
                PaymentApprovedEvent.EVENT_TYPE,
                Instant.now(),
                saved.getId(),
                "tx-12345",
                new BigDecimal("50.00")
        );

        kafkaTemplate.send(paymentApprovedTopic, saved.getId().toString(), event);

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> {
                    Order updated = orderGateway.findById(saved.getId()).orElseThrow();
                    assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
                });
    }
}
