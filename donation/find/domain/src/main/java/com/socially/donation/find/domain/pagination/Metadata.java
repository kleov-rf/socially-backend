package com.socially.donation.find.domain.pagination;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Metadata {
  private final String nextCursor;
  private final String previousCursor;
  private final int size;
  private final long totalCount;

  public static Metadata create(
      String nextCursor, String previousCursor, Integer size, Long totalCount) {
    validate(size, totalCount);
    return new Metadata(
        normalizeCursor(nextCursor), normalizeCursor(previousCursor), size, totalCount);
  }

  private static void validate(Integer size, Long totalCount) {
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be greater than zero");
    }

    if (totalCount < 0) {
      throw new IllegalArgumentException("Total count must be greater than or equal to zero");
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

  public long totalCount() {
    return totalCount;
  }
}
