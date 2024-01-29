package org.kafka.exp.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.kafka.exp.model.Order;

public class OrderDeserializer extends ObjectMapperDeserializer<Order> {

  public OrderDeserializer() {
    super(Order.class);
  }
}
