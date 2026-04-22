package com.socially.donation.find.domain.pagination;

import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PaginationCriteria {
  public static final int DEFAULT_SIZE = 5;
  public static final DonationsOrder DEFAULT_ORDER = DonationsOrder.NEWEST_FIRST;
  private static final Set<Integer> ALLOWED_SIZES = Set.of(5, 10, 20);

  private final String cursor;
  private final int size;
  private final DonationsOrder order;
  private final String query;

  public static PaginationCriteria create(
      String cursor, Integer size, DonationsOrder order, String query) {
    validate(size);
    String normalizedQuery = normalizeQuery(query);
    if (Objects.nonNull(cursor) && cursor.isBlank()) {
      return new PaginationCriteria(null, size, order, normalizedQuery);
    }
    return new PaginationCriteria(cursor, size, order, normalizedQuery);
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

  private static void validate(Integer size) {
    if (!ALLOWED_SIZES.contains(size)) {
      throw new IllegalArgumentException("Size must be one of: 5, 10, 20");
    }
  }

  public String cursor() {
    return cursor;
  }

  public int size() {
    return size;
  }

  public DonationsOrder order() {
    return order;
  }

  public String query() {
    return query;
  }
}
