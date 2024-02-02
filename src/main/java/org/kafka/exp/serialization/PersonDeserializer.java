package org.kafka.exp.serialization;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import org.kafka.exp.model.Person;

public class PersonDeserializer extends ObjectMapperDeserializer<Person> {

  public PersonDeserializer() {
    super(Person.class);
  }
}
