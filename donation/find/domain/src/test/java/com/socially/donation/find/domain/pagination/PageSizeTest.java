package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageSizeTest {

  @Test
  void fromValue_should_return_matching_page_size() {
    assertEquals(PageSize.FIVE_ITEMS, PageSize.fromValue(5));
    assertEquals(PageSize.TEN_ITEMS, PageSize.fromValue(10));
    assertEquals(PageSize.TWENTY_ITEMS, PageSize.fromValue(20));
  }

  @Test
  void fromValue_should_throw_when_value_is_not_allowed() {
    assertThrows(IllegalArgumentException.class, () -> PageSize.fromValue(0));
    assertThrows(IllegalArgumentException.class, () -> PageSize.fromValue(null));
  }
}
