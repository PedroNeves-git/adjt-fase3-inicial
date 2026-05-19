package br.com.restaurant.pagamento.infra.messaging;

import br.com.restaurant.pagamento.application.port.out.ExternalPaymentProcessorPort;
import br.com.restaurant.pagamento.domain.model.ExternalPaymentResult;
import br.com.restaurant.pagamento.infra.messaging.consumer.dto.OrderCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"pedido.criado", "pedido.criado-dlt", "pagamento.aprovado", "pagamento.pendente"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("test")
@DirtiesContext
@DisplayName("Kafka Integration — OrderCreatedConsumer")
class OrderCreatedConsumerIT {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @MockBean
    private ExternalPaymentProcessorPort externalProcessor;

    @Test
    @DisplayName("should publish pagamento.aprovado when pedido.criado is consumed successfully")
    void shouldPublishPaymentApprovedWhenOrderCreated() throws Exception {
        String orderId = "order-kafka-it-001";
        BigDecimal amount = new BigDecimal("150.00");

        when(externalProcessor.process(eq(orderId), any(BigDecimal.class)))
                .thenReturn(ExternalPaymentResult.approved("txn-kafka-it-001"));

        Map<String, Object> props = KafkaTestUtils.consumerProps("test-verifier", "true", embeddedKafka);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        Consumer<String, String> verifier = new DefaultKafkaConsumerFactory<String, String>(props)
                .createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(verifier, "pagamento.aprovado");

        kafkaTemplate.send("pedido.criado", orderId, buildEvent(orderId, amount))
                .get(5, TimeUnit.SECONDS);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(verifier);

        assertThat(records.count()).isGreaterThan(0);
        ConsumerRecord<String, String> record = records.iterator().next();
        assertThat(record.key()).isEqualTo(orderId);
        assertThat(record.value())
                .contains("PAGAMENTO_APROVADO")
                .contains("txn-kafka-it-001")
                .contains(orderId);

        verifier.close();
    }

    private OrderCreatedEvent buildEvent(String orderId, BigDecimal amount) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("PEDIDO_CRIADO");
        event.setTimestamp(Instant.now().toString());
        event.setOrderId(orderId);
        event.setTotalAmount(amount);
        return event;
    }
}
