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
  void create_should_throw_exception_if_size_is_not_allowed() {
    IllegalArgumentException zeroSizeException =
        assertThrows(IllegalArgumentException.class, () -> PaginationCriteria.create(null, 0));
    IllegalArgumentException oneSizeException =
        assertThrows(IllegalArgumentException.class, () -> PaginationCriteria.create(null, 1));
    IllegalArgumentException twentyFiveSizeException =
        assertThrows(IllegalArgumentException.class, () -> PaginationCriteria.create(null, 25));

    assertEquals("Size must be one of: 5, 10, 20", zeroSizeException.getMessage());
    assertEquals("Size must be one of: 5, 10, 20", oneSizeException.getMessage());
    assertEquals("Size must be one of: 5, 10, 20", twentyFiveSizeException.getMessage());
  }

  @Test
  void create_should_create_criteria_with_null_cursor_if_cursor_is_blank() {
    PaginationCriteria criteria = PaginationCriteria.create("   ", 10);

    assertNull(criteria.cursor());
    assertEquals(10, criteria.size());
  }

  @Test
  void create_should_accept_all_allowed_sizes() {
    assertEquals(5, PaginationCriteria.create(null, 5).size());
    assertEquals(10, PaginationCriteria.create(null, 10).size());
    assertEquals(20, PaginationCriteria.create(null, 20).size());
  }
}
