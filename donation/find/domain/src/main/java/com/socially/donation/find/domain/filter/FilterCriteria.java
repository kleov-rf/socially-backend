package com.socially.donation.find.domain.filter;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FilterCriteria(Optional<String> query) {

  public static FilterCriteria create() {
    return new FilterCriteria(Optional.empty());
  }

  public FilterCriteria withQuery(Optional<String> query) {
    return new FilterCriteria(normalizeQuery(query));
  }

  private static Optional<String> normalizeQuery(Optional<String> query) {
    return query.map(String::trim).filter(s -> !s.isBlank());
  }
}
