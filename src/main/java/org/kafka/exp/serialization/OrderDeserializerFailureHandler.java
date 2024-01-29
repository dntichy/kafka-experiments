package org.kafka.exp.serialization;

import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.kafka.exp.model.Order;
import org.kafka.exp.serialization.failurehandler.DeserializationHandler;

@Identifier("order-serialization-failure-handler")
@ApplicationScoped
public class OrderDeserializerFailureHandler extends DeserializationHandler<Order> {}
