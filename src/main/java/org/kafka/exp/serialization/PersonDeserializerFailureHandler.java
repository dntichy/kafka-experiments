package org.kafka.exp.serialization;

import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.kafka.exp.model.Person;
import org.kafka.exp.serialization.failurehandler.DeserializationHandler;

@Identifier("person-serialization-failure-handler")
@ApplicationScoped
public class PersonDeserializerFailureHandler extends DeserializationHandler<Person> {}
