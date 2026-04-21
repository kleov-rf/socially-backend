package com.socially.donation.find.domain.pagination;

import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PaginationCriteria {
  public static final int DEFAULT_SIZE = 5;
  private static final Set<Integer> ALLOWED_SIZES = Set.of(5, 10, 20);

  private final String cursor;
  private final int size;

  public static PaginationCriteria create(String cursor, Integer size) {
    validate(size);
    if (Objects.nonNull(cursor) && cursor.isBlank()) {
      return new PaginationCriteria(null, size);
    }
    return new PaginationCriteria(cursor, size);
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
}
