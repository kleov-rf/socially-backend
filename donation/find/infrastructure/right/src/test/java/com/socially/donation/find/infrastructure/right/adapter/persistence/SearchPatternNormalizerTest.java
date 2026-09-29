package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donation.find.domain.filter.FilterCriteria;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SearchPatternNormalizerTest {
  private final SearchPatternNormalizer sut = new SearchPatternNormalizer();

  @Test
  void toSearchPattern_should_return_empty_when_query_is_empty() {
    assertEquals(Optional.empty(), sut.toSearchPattern(FilterCriteria.create()));
  }

  @Test
  void toSearchPattern_should_return_lowercase_wrapped_pattern_when_query_present() {
    assertEquals(
        Optional.of("%school uniforms%"),
        sut.toSearchPattern(FilterCriteria.create().withQuery(Optional.of("School Uniforms"))));
  }
}
