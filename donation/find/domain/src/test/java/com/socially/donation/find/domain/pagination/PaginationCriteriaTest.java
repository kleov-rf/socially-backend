package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PaginationCriteriaTest {

  @Test
  void create_should_default_order_to_newest_first() {
    PaginationCriteria criteria = PaginationCriteria.create(null, 5);

    assertEquals(DonationsOrder.NEWEST_FIRST, criteria.order());
  }

  @Test
  void create_should_create_criteria_with_received_cursor() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", 10, DonationsOrder.OLDEST_FIRST);

    assertEquals("cursor-token", criteria.cursor());
  }

  @Test
  void create_should_create_criteria_with_null_cursor_if_cursor_is_blank() {
    PaginationCriteria criteria = PaginationCriteria.create("   ", 10);

    assertNull(criteria.cursor());
  }

  @ParameterizedTest
  @CsvSource({"5", "10", "20"})
  void create_should_create_criteria_with_received_size(Integer size) {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", size, DonationsOrder.OLDEST_FIRST);

    assertEquals(size, criteria.size());
  }

  @Test
  void create_should_create_criteria_with_received_order() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", 10, DonationsOrder.OLDEST_FIRST);

    assertEquals(DonationsOrder.OLDEST_FIRST, criteria.order());
  }

  @Test
  void create_should_throw_exception_if_size_is_not_allowed() {
    assertThrows(IllegalArgumentException.class, () -> PaginationCriteria.create(null, 0));
  }

  @Test
  void cursor_should_return_cursor() {
    PaginationCriteria criteria = PaginationCriteria.create("cursor-token", 5);

    assertEquals("cursor-token", criteria.cursor());
  }

  @Test
  void size_should_return_size() {
    PaginationCriteria criteria = PaginationCriteria.create("cursor-token", 10);

    assertEquals(10, criteria.size());
  }

  @Test
  void order_should_return_order() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", 10, DonationsOrder.OLDEST_FIRST);

    assertEquals(DonationsOrder.OLDEST_FIRST, criteria.order());
  }
}
