package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageOrderTest {

  @Test
  void fromValue_should_return_newest_first_when_value_is_null() {
    assertEquals(PageOrder.NEWEST_FIRST, PageOrder.fromValue(null));
  }

  @Test
  void fromValue_should_return_newest_first_if_received_newest_first() {
    assertEquals(PageOrder.NEWEST_FIRST, PageOrder.fromValue("newest_first"));
  }

  @Test
  void fromValue_should_return_oldest_first_if_received_oldest_first() {
    assertEquals(PageOrder.OLDEST_FIRST, PageOrder.fromValue("oldest_first"));
  }

  @Test
  void fromValue_should_return_nearest_first_if_received_nearest_first() {
    assertEquals(PageOrder.NEAREST_FIRST, PageOrder.fromValue("nearest_first"));
  }

  @Test
  void value_should_return_newest_first() {
    assertEquals("newest_first", PageOrder.NEWEST_FIRST.value());
  }

  @Test
  void value_should_return_oldest_first() {
    assertEquals("oldest_first", PageOrder.OLDEST_FIRST.value());
  }

  @Test
  void value_should_return_nearest_first() {
    assertEquals("nearest_first", PageOrder.NEAREST_FIRST.value());
  }

  @Test
  void fromValue_should_throw_for_unsupported_value() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> PageOrder.fromValue("invalid"));

    assertEquals(
        "Order must be one of: newest_first, oldest_first, nearest_first", exception.getMessage());
  }
}
