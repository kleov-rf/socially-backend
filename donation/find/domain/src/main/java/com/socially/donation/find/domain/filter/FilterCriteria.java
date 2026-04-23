package com.socially.donation.find.domain.filter;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class FilterCriteria {
  private final String query;

  public static FilterCriteria create(String query) {
    return new FilterCriteria(normalizeQuery(query));
  }

  private static String normalizeQuery(String query) {
    if (Objects.isNull(query)) {
      return null;
    }
    String trimmed = query.trim();
    if (trimmed.isBlank()) {
      return null;
    }
    return trimmed;
  }

  public String query() {
    return query;
  }
}
