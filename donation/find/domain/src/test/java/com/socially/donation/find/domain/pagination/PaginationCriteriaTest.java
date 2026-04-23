package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PaginationCriteriaTest {

  @Test
  void create_should_default_order_to_newest_first() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    assertEquals(PageOrder.NEWEST_FIRST, criteria.order());
  }

  @Test
  void create_should_create_criteria_with_received_cursor() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", PageSize.TEN_ITEMS, PageOrder.OLDEST_FIRST);

    assertEquals("cursor-token", criteria.cursor());
  }

  @Test
  void create_should_create_criteria_with_null_cursor_if_cursor_is_blank() {
    PaginationCriteria criteria =
        PaginationCriteria.create("   ", PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    assertNull(criteria.cursor());
  }

  @ParameterizedTest
  @CsvSource({"5", "10", "20"})
  void create_should_create_criteria_with_received_size(Integer size) {
    PageSize pageSize = PageSize.fromValue(size);
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", pageSize, PageOrder.OLDEST_FIRST);

    assertEquals(size, criteria.size());
  }

  @Test
  void create_should_create_criteria_with_received_order() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", PageSize.TEN_ITEMS, PageOrder.OLDEST_FIRST);

    assertEquals(PageOrder.OLDEST_FIRST, criteria.order());
  }

  @Test
  void cursor_should_return_cursor() {
    PaginationCriteria criteria =
        PaginationCriteria.create(
            "cursor-token", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    assertEquals("cursor-token", criteria.cursor());
  }

  @Test
  void size_should_return_size() {
    PaginationCriteria criteria =
        PaginationCriteria.create(
            "cursor-token", PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    assertEquals(10, criteria.size());
  }

  @Test
  void order_should_return_order() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor-token", PageSize.TEN_ITEMS, PageOrder.OLDEST_FIRST);

    assertEquals(PageOrder.OLDEST_FIRST, criteria.order());
  }
}
