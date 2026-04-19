package com.socially.donation.find.domain.pagination;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Metadata {
  private final String nextCursor;
  private final boolean hasNext;
  private final int size;

  public static Metadata create(String nextCursor, Boolean hasNext, Integer size) {
    validate(nextCursor, hasNext, size);
    return new Metadata(normalizeCursor(nextCursor), hasNext, size);
  }

  private static void validate(String nextCursor, Boolean hasNext, Integer size) {
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be greater than zero");
    }
    if (hasNext && normalizeCursor(nextCursor) == null) {
      throw new IllegalArgumentException("Next cursor is required when hasNext is true");
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

  public boolean hasNext() {
    return hasNext;
  }

  public int size() {
    return size;
  }
}
