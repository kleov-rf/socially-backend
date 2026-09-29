package com.socially.donation.find.domain.pagination;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Metadata(
    int size, long totalCount, Optional<String> nextCursor, Optional<String> previousCursor) {

  public static Metadata create(int size, long totalCount) {
    validate(size, totalCount);
    return new Metadata(size, totalCount, Optional.empty(), Optional.empty());
  }

  public Metadata withNextCursor(Optional<String> nextCursor) {
    return new Metadata(size, totalCount, normalizeCursor(nextCursor), previousCursor);
  }

  public Metadata withPreviousCursor(Optional<String> previousCursor) {
    return new Metadata(size, totalCount, nextCursor, normalizeCursor(previousCursor));
  }

  private static void validate(int size, long totalCount) {
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be greater than zero");
    }

    if (totalCount < 0) {
      throw new IllegalArgumentException("Total count must be greater than or equal to zero");
    }
  }

  private static Optional<String> normalizeCursor(Optional<String> cursor) {
    return cursor.map(String::trim).filter(s -> !s.isBlank());
  }

  public boolean hasNext() {
    return nextCursor.isPresent();
  }

  public boolean hasPrevious() {
    return previousCursor.isPresent();
  }
}
