package br.com.restaurant.pagamento.infra.config;

import br.com.restaurant.pagamento.infra.messaging.consumer.dto.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    // ─── Producer ────────────────────────────────────────────────────────────

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // ─── Consumer ────────────────────────────────────────────────────────────

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Manual commit — offset committed only after successful listener return
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        JsonDeserializer<OrderCreatedEvent> deserializer =
                new JsonDeserializer<>(OrderCreatedEvent.class, false);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    // ─── Error Handler with Retry + DLT ──────────────────────────────────────

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<String, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate) {
                    @Override
                    public void accept(
                            org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record,
                            org.apache.kafka.clients.consumer.Consumer<?, ?> consumer,
                            Exception exception) {
                        try {
                            super.accept(record, consumer, exception);
                            log.info("[KafkaConfig] Mensagem enviada ao DLT com sucesso. topic={}, offset={}", record.topic(), record.offset());
                        } catch (Exception e) {
                            // Se falhar ao publicar no DLT, loga e descarta para não reiniciar o ciclo de retries
                            log.error("[KafkaConfig] Falha ao publicar no DLT. topic={}, offset={}, erro={}", record.topic(), record.offset(), e.getMessage());
                        }
                    }
                };

        ExponentialBackOff backOff = new ExponentialBackOff(1_000L, 2.0);
        backOff.setMaxAttempts(4);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
        handler.addNotRetryableExceptions(
                org.springframework.kafka.support.serializer.DeserializationException.class
        );

        // Handler para tratar exception que aparece enquanto retry está ocorrendo
        handler.setLogLevel(org.springframework.kafka.KafkaException.Level.WARN);
        handler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("[KafkaConfig] Retry {}/{} para orderId={}. Motivo: {}",
                        deliveryAttempt,
                        backOff.getMaxAttempts() + 1,
                        record.key(),
                        ex.getMessage())
        );

        handler.addNotRetryableExceptions(
                org.springframework.kafka.support.serializer.DeserializationException.class
        );

        return handler;
    }

    // ─── Listener Container Factory ───────────────────────────────────────────

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    orderCreatedListenerContainerFactory(DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedConsumerFactory());
        factory.setCommonErrorHandler(errorHandler);
        // AckMode RECORD: commit offset only after listener returns without exception
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        return factory;
    }

    // ─── DLT Listener Container Factory ──────────────────────────────────────

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
    dltListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        return factory;
    }

    // ─── Topics ──────────────────────────────────────────────────────────────

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    public NewTopic topicOrderCreated(@Value("${kafka.topics.pedido-criado}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicOrderCreatedDlt() {
        // Declared explicitly to guarantee correct configuration
        return new NewTopic("pedido.criado.DLT", 1, (short) 1);
    }

    @Bean
    public NewTopic topicPaymentApproved(@Value("${kafka.topics.pagamento-aprovado}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    public NewTopic topicPaymentPending(@Value("${kafka.topics.pagamento-pendente}") String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }
}
