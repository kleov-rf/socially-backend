package com.socially.donation.find.domain.pagination;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Metadata {
  private final String nextCursor;
  private final String previousCursor;
  private final int size;

  public static Metadata create(String nextCursor, String previousCursor, Integer size) {
    validate(size);
    return new Metadata(normalizeCursor(nextCursor), normalizeCursor(previousCursor), size);
  }

  private static void validate(Integer size) {
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be greater than zero");
    }
  }

  private static String normalizeCursor(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }
    return cursor;
  }

  public String nextCursor() {
    return nextCursor;
  }

  public String previousCursor() {
    return previousCursor;
  }

  public boolean hasNext() {
    return Objects.nonNull(nextCursor);
  }

  public boolean hasPrevious() {
    return Objects.nonNull(previousCursor);
  }

  public int size() {
    return size;
  }
}
