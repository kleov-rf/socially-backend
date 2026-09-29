package com.socially.donation.find.domain.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class FilterCriteriaTest {

  @Test
  void create_should_create_criteria_with_trimmed_query() {
    FilterCriteria criteria = FilterCriteria.create().withQuery(Optional.of("   school    "));

    assertEquals(Optional.of("school"), criteria.query());
  }

  @Test
  void create_should_create_criteria_with_empty_query_if_query_is_blank() {
    FilterCriteria criteria = FilterCriteria.create().withQuery(Optional.of("      "));

    assertEquals(Optional.empty(), criteria.query());
  }

  @Test
  void create_should_create_criteria_with_empty_query_if_query_not_present() {
    FilterCriteria criteria = FilterCriteria.create();

    assertEquals(Optional.empty(), criteria.query());
  }

  @Test
  void query_should_return_query() {
    FilterCriteria criteria = FilterCriteria.create().withQuery(Optional.of("school"));

    assertEquals(Optional.of("school"), criteria.query());
  }
}
