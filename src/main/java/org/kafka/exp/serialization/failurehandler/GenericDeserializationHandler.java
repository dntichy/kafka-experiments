package org.kafka.exp.serialization.failurehandler;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.common.header.Headers;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
public class GenericDeserializationHandler {

  @Inject
  @Channel("serialization-failure")
  Emitter<String> dlqEmitter;

  public <T> T handle(
      Uni<T> valueForDeserialization,
      String topic,
      boolean isKey,
      String deserializer,
      byte[] data,
      Headers headers) {
    return valueForDeserialization
        .onFailure()
        .recoverWithItem(
            (throwable) -> {
              appendHeaders(topic, isKey, deserializer, data, headers, throwable);
              sendToDLQ(data, headers);
              return null;
            })
        .await()
        .indefinitely();
  }

  void appendHeaders(
      String topic,
      boolean isKey,
      String deserializer,
      byte[] data,
      Headers headers,
      Throwable throwable) {
    DeserializationFailureHandler.addFailureDetailsToHeaders(
        deserializer, topic, isKey, headers, data, throwable);
  }

  void sendToDLQ(byte[] data, Headers headers) {
    dlqEmitter.send(
        Message.of(new String(data))
            .addMetadata(OutgoingKafkaRecordMetadata.builder().withHeaders(headers).build()));
  }
}
