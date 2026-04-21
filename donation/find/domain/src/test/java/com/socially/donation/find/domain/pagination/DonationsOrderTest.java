package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DonationsOrderTest {

  @Test
  void fromValue_should_return_newest_first_when_value_is_null() {
    assertEquals(DonationsOrder.NEWEST_FIRST, DonationsOrder.fromValue(null));
  }

  @Test
  void fromValue_should_return_newest_first_if_received_newest_first() {
    assertEquals(DonationsOrder.NEWEST_FIRST, DonationsOrder.fromValue("newest_first"));
  }

  @Test
  void fromValue_should_return_oldest_first_if_received_oldest_first() {
    assertEquals(DonationsOrder.OLDEST_FIRST, DonationsOrder.fromValue("oldest_first"));
  }

  @Test
  void fromValue_should_throw_for_unsupported_value() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> DonationsOrder.fromValue("invalid"));

    assertEquals("Order must be one of: newest_first, oldest_first", exception.getMessage());
  }
}
