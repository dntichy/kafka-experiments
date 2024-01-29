package org.kafka.exp.serialization.failurehandler;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler;
import jakarta.inject.Inject;
import org.apache.kafka.common.header.Headers;

public class DeserializationHandler<T> implements DeserializationFailureHandler<T> {
  @Inject GenericDeserializationHandler genericDeserializationHandler;

  @Override
  public T decorateDeserialization(
      Uni<T> deserialization,
      String topic,
      boolean isKey,
      String deserializer,
      byte[] data,
      Headers headers) {
    return genericDeserializationHandler.handle(
        deserialization, topic, isKey, deserializer, data, headers);
  }
}
