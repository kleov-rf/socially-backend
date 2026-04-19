package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PaginationCriteriaTest {

  @Test
  void create_should_create_criteria() {
    PaginationCriteria criteria = PaginationCriteria.create("cursor-token", 10);

    assertEquals("cursor-token", criteria.cursor());
    assertEquals(10, criteria.size());
  }

  @Test
  void create_should_throw_exception_if_size_is_less_or_equal_to_zero() {
    assertThrows(IllegalArgumentException.class, () -> PaginationCriteria.create(null, 0));
    assertThrows(IllegalArgumentException.class, () -> PaginationCriteria.create(null, -1));
  }

  @Test
  void create_should_throw_exception_if_size_is_greater_than_max_size() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PaginationCriteria.create(null, PaginationCriteria.MAX_SIZE + 1));
  }

  @Test
  void create_should_create_criteria_with_null_cursor_if_cursor_is_blank() {
    PaginationCriteria criteria = PaginationCriteria.create("   ", 10);

    assertNull(criteria.cursor());
    assertEquals(10, criteria.size());
  }
}
