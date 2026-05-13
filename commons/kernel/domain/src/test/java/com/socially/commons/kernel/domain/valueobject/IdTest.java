package com.socially.commons.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class IdTest {
  @Test
  void from_should_return_id() {
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";

    Id id = Id.from(uuidString);

    assertEquals(uuidString, id.value().toString());
  }

  @Test
  void generate_should_return_id() {
    Id id = Id.generate();

    assertNotNull(id);
  }

  @Test
  void generate_should_return_id_with_non_null_value() {
    Id id = Id.generate();

    assertNotNull(id.value());
  }

  @Test
  void generate_should_return_different_values_for_each_invocation() {
    Id first = Id.generate();
    Id second = Id.generate();

    assertNotEquals(first.value(), second.value());
  }

  @Test
  void value_should_return_value() {
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";
    Id id = Id.from(uuidString);

    assertEquals(uuidString, id.value().toString());
  }
}
