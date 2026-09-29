package com.socially.donation.find.infrastructure.right.adapter.persistence;

import java.util.Optional;

public record CursorMetadata(Optional<String> nextCursor, Optional<String> previousCursor) {

  public static CursorMetadata create() {
    return new CursorMetadata(Optional.empty(), Optional.empty());
  }

  public CursorMetadata withNextCursor(Optional<String> nextCursor) {
    return new CursorMetadata(nextCursor, previousCursor);
  }

  public CursorMetadata withPreviousCursor(Optional<String> previousCursor) {
    return new CursorMetadata(nextCursor, previousCursor);
  }
}
