package com.socially.donation.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TitleTest {

  @Test
  void from_should_return_title() {
    Title title = Title.from("Community Garden");

    assertEquals("Community Garden", title.value());
  }

  @Test
  void from_should_trim_valid_value() {
    Title title = Title.from("  Community Garden  ");

    assertEquals("Community Garden", title.value());
  }

  @Test
  void from_should_throw_when_value_is_null() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Title.from(null));

    assertEquals("title cannot be null", exception.getMessage());
  }

  @Test
  void from_should_throw_when_value_is_blank() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Title.from("   "));

    assertEquals("title cannot be blank", exception.getMessage());
  }
}
