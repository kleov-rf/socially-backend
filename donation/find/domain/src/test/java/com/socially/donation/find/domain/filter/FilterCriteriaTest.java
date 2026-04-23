package com.socially.donation.find.domain.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FilterCriteriaTest {

  @Test
  void create_should_create_criteria_with_trimmed_query() {
    FilterCriteria criteria = FilterCriteria.create("   school    ");

    assertEquals("school", criteria.query());
  }

  @Test
  void create_should_create_criteria_with_null_query_if_query_is_blank() {
    FilterCriteria criteria = FilterCriteria.create("      ");

    assertNull(criteria.query());
  }

  @Test
  void create_should_create_criteria_with_null_query_if_query_not_present() {
    FilterCriteria criteria = FilterCriteria.create(null);

    assertNull(criteria.query());
  }

  @Test
  void query_should_return_query() {
    FilterCriteria criteria = FilterCriteria.create("school");

    assertEquals("school", criteria.query());
  }
}
