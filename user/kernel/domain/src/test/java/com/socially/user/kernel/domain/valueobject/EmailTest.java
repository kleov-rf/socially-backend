package com.socially.user.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  void from_should_throw_exception_when_received_value_is_null() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Email.from(null));

    assertEquals("email cannot be blank", exception.getMessage());
  }

  @Test
  void from_should_throw_exception_when_received_value_is_blank() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Email.from("   "));

    assertEquals("email cannot be blank", exception.getMessage());
  }

  @Test
  void from_should_return_email() {
    Email email = Email.from("user@example.com");

    assertEquals("user@example.com", email.value());
  }

  @Test
  void from_should_return_email_trimmed_when_surrounded_by_blank_characters() {
    Email email = Email.from("  user@example.com  ");

    assertEquals("user@example.com", email.value());
  }

  @Test
  void value_should_return_current_value() {
    Email email = Email.from("user@example.com");

    assertEquals("user@example.com", email.value());
  }
}
