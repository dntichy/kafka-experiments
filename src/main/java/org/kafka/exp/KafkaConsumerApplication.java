package org.kafka.exp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kafka.exp.model.Order;
import org.kafka.exp.model.Person;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KafkaConsumerApplication {

  @Inject Logger logger;

  @Incoming("persons")
  public void consumePerson(ConsumerRecord<String, Person> message) {
    if (message.value() == null) {
      logger.info("Deserialization error " + message.value());
      return;
    }

    logger.info(message.value());
  }

  @Incoming("orders")
  public void consumeOrders(ConsumerRecord<String, Order> message) {
    if (message.value() == null) {
      logger.info("Deserialization error " + message.value());
      return;
    }

    logger.info(message.value());
  }
}
