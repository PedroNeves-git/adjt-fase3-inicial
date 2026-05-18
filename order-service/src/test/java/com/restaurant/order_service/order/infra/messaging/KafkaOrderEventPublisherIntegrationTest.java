package com.restaurant.order_service.order.infra.messaging;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.dto.event.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        topics = {"pedido.criado"},
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("test")
class KafkaOrderEventPublisherIntegrationTest {

    @Autowired
    private KafkaOrderEventPublisher publisher;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Test
    @DisplayName("publishes ORDER_CREATED event with order id as key and full payload as value")
    void publishesOrderCreatedEvent() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-grp", "true", broker);
        DefaultKafkaConsumerFactory<String, OrderCreatedEvent> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(OrderCreatedEvent.class).trustedPackages("*")
        );

        try (Consumer<String, OrderCreatedEvent> consumer = cf.createConsumer()) {
            broker.consumeFromAnEmbeddedTopic(consumer, "pedido.criado");

            UUID orderId = UUID.randomUUID();
            UUID itemId = UUID.randomUUID();
            UUID restaurantId = UUID.randomUUID();
            UUID menuItemId = UUID.randomUUID();

            Order order = Order.newOrder(42L, restaurantId, List.of(
                    OrderItem.create(menuItemId, "X-Burger", 2, new BigDecimal("25.00"))
            ));
            order.assignId(orderId);
            order.getItems().get(0).assignId(itemId);
            order.confirm();

            publisher.publishOrderCreated(order);

            ConsumerRecord<String, OrderCreatedEvent> record = KafkaTestUtils.getSingleRecord(
                    consumer, "pedido.criado", Duration.ofSeconds(10)
            );

            assertThat(record.key()).isEqualTo(orderId.toString());

            OrderCreatedEvent event = record.value();
            assertThat(event.eventId()).isNotNull();
            assertThat(event.eventType()).isEqualTo("ORDER_CREATED");
            assertThat(event.timestamp()).isNotNull();
            assertThat(event.orderId()).isEqualTo(orderId);
            assertThat(event.clientId()).isEqualTo(42L);
            assertThat(event.restaurantId()).isEqualTo(restaurantId);
            assertThat(event.totalAmount()).isEqualByComparingTo("50.00");

            assertThat(event.items()).hasSize(1);
            OrderCreatedEvent.Item item = event.items().get(0);
            assertThat(item.menuItemId()).isEqualTo(menuItemId);
            assertThat(item.name()).isEqualTo("X-Burger");
            assertThat(item.quantity()).isEqualTo(2);
            assertThat(item.unitPrice()).isEqualByComparingTo("25.00");
        }
    }
}
