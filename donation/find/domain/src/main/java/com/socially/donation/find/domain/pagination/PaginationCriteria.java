package com.socially.donation.find.domain.pagination;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PaginationCriteria {
  public static final int DEFAULT_SIZE = 5;
  public static final int MAX_SIZE = 100;

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
    if (size <= 0 || size > MAX_SIZE) {
      throw new IllegalArgumentException("Size must be between 1 and " + MAX_SIZE);
    }
  }

  public String cursor() {
    return cursor;
  }

  public int size() {
    return size;
  }
}
