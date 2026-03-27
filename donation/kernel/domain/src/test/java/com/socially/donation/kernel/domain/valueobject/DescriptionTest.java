package com.socially.donation.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DescriptionTest {

  @Test
  void from_should_return_description() {
    Description description = Description.from("Donations for books");

    assertEquals("Donations for books", description.value());
  }

  @Test
  void from_should_trim_valid_value() {
    Description description = Description.from("  Donations for books  ");

    assertEquals("Donations for books", description.value());
  }

  @Test
  void from_should_throw_when_value_is_null() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Description.from(null));

    assertEquals("description cannot be null", exception.getMessage());
  }

  @Test
  void from_should_throw_when_value_is_blank() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Description.from("  "));

    assertEquals("description cannot be blank", exception.getMessage());
  }
}
