package org.kafka.exp;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaLifecycleManager implements QuarkusTestResourceLifecycleManager {

  public static final String TOPIC_ORDERS = "orders";
  public static final String TOPIC_DLQ = "serialization-failure";
  private static final List<String> TOPICS = List.of(TOPIC_ORDERS, TOPIC_DLQ);

  private static String KAFKA_BOOTSTRAP_SERVERS = null;

  private KafkaContainer kafkaContainer;

  @Override
  public Map<String, String> start() {
    kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.2"));
    kafkaContainer.start();

    KAFKA_BOOTSTRAP_SERVERS = kafkaContainer.getBootstrapServers();

    createTopics();
    return getServicePropertiesToOverride();
  }

  @Override
  public void stop() {
    if (kafkaContainer != null) {
      kafkaContainer.stop();
    }
  }

  public static Producer<String, String> createOrderProducer() {
    final Properties props = new Properties();
    props.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaLifecycleManager.getKafkaBootstrapServers());
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "test-producer");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ObjectMapperSerializer.class.getName());

    return new KafkaProducer<>(props);
  }

  public static KafkaConsumer<String, String> createDLQConsumer() {
    Properties props = new Properties();
    props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaLifecycleManager.getKafkaBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(TOPIC_DLQ));
    return consumer;
  }

  public static String getKafkaBootstrapServers() {
    return KAFKA_BOOTSTRAP_SERVERS;
  }

  public static void recreateTopics() {
    removeTopics();
    createTopics();
  }

  private static void createTopics() {
    Properties kafkaProperties = new Properties();
    kafkaProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
    List<NewTopic> newTopics =
        TOPICS.stream()
            .map(topic -> new NewTopic(topic, Optional.of(1), Optional.empty()))
            .collect(Collectors.toList());
    try (Admin admin = Admin.create(kafkaProperties)) {
      admin.createTopics(newTopics);
    }
  }

  private static void removeTopics() {
    Properties kafkaProperties = new Properties();
    kafkaProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
    try (Admin admin = Admin.create(kafkaProperties)) {
      admin.deleteTopics(TOPICS);
    }
  }

  private static Map<String, String> getServicePropertiesToOverride() {
    Map<String, String> properties = new HashMap<>();
    properties.put("kafka.bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS.split("://")[1]);
    return properties;
  }
}
