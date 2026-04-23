package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.donation.find.domain.filter.FilterCriteria;
import org.junit.jupiter.api.Test;

class SearchPatternNormalizerTest {
  private final SearchPatternNormalizer sut = new SearchPatternNormalizer();

  @Test
  void toSearchPattern_should_return_null_when_filter_is_null() {
    assertNull(sut.toSearchPattern(null));
  }

  @Test
  void toSearchPattern_should_return_null_when_query_is_null() {
    assertNull(sut.toSearchPattern(FilterCriteria.create(null)));
  }

  @Test
  void toSearchPattern_should_return_lowercase_wrapped_pattern_when_query_present() {
    assertEquals(
        "%school uniforms%", sut.toSearchPattern(FilterCriteria.create("School Uniforms")));
  }
}
