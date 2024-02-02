package org.kafka.exp;

import static org.kafka.exp.KafkaLifecycleManager.TOPIC_ORDERS;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(KafkaLifecycleManager.class)
class KafkaConsumerApplicationIT {

  private static Producer<String, String> orderProducer;
  private static KafkaConsumer<String, String> dlqConsumer;

  @BeforeAll
  static void beforeAll() {
    KafkaLifecycleManager.recreateTopics();
    orderProducer = KafkaLifecycleManager.createOrderProducer();
    dlqConsumer = KafkaLifecycleManager.createDLQConsumer();
  }

  @AfterAll
  static void afterAll() {
    orderProducer.close();
    dlqConsumer.close();
  }

  @Test
  void payloadShouldNotBeNull_whenDeserializationWasSuccess() {
    orderProducer.send(new ProducerRecord<>(TOPIC_ORDERS, 0, 0L, "key", "{0}", new HashSet<>()));
    var dlqRecord = pollRecord(dlqConsumer);
    System.out.println();
  }

  private Optional<ConsumerRecord<String, String>> pollRecord(
      KafkaConsumer<String, String> consumer) {
    final var records = consumer.poll(Duration.ofMillis(5000));
    final var iterator = records.iterator();
    return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
  }
}
