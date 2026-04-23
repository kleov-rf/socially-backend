package com.socially.donation.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IdTest {
  @Test
  void value_should_return_value() {
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";
    Id id = Id.from(uuidString);

    assertEquals(uuidString, id.value().toString());
  }
}
