package org.kafka.exp.serialization.failurehandler;

import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler;
import jakarta.inject.Inject;
import org.apache.kafka.common.header.Headers;

public class DeserializationHandler<T> implements DeserializationFailureHandler<T> {
  @Inject GenericDeserializationHandler genericDeserializationHandler;

  @Override
  public T handleDeserializationFailure(
      String topic,
      boolean isKey,
      String deserializer,
      byte[] data,
      Exception exception,
      Headers headers) {
    return genericDeserializationHandler.handle(data, headers);
  }
}
