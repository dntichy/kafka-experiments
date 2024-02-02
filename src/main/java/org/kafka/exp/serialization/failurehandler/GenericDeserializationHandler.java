package org.kafka.exp.serialization.failurehandler;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.header.Headers;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@ApplicationScoped
public class GenericDeserializationHandler {

  @Inject
  @Channel(value = "serialization-failure")
  Emitter<String> dlqEmitter;

  public <T> T handle(byte[] data, Headers headers) {
    sendToDLQ(data, headers);
    return null;
  }

  void sendToDLQ(byte[] data, Headers headers) {
    dlqEmitter.send(
        Message.of(new String(data, StandardCharsets.UTF_8))
            .addMetadata(OutgoingKafkaRecordMetadata.builder().withHeaders(headers).build()));
  }
}
