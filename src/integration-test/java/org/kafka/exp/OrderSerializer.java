package org.kafka.exp;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import org.kafka.exp.model.Order;

public class OrderSerializer extends ObjectMapperSerializer<Order> {

  public OrderSerializer() {
    super();
  }
}
