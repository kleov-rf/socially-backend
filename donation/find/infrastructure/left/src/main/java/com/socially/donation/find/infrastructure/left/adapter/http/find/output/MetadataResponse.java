package com.socially.donation.find.infrastructure.left.adapter.http.find.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record MetadataResponse(
    Boolean hasNext,
    Boolean hasPrevious,
    Integer size,
    Long totalCount,
    Optional<String> nextCursor,
    Optional<String> previousCursor) {

  public static MetadataResponse create(
      Boolean hasNext, Boolean hasPrevious, Integer size, Long totalCount) {
    return new MetadataResponse(
        hasNext, hasPrevious, size, totalCount, Optional.empty(), Optional.empty());
  }

  public MetadataResponse withNextCursor(Optional<String> nextCursor) {
    return new MetadataResponse(hasNext, hasPrevious, size, totalCount, nextCursor, previousCursor);
  }

  public MetadataResponse withPreviousCursor(Optional<String> previousCursor) {
    return new MetadataResponse(hasNext, hasPrevious, size, totalCount, nextCursor, previousCursor);
  }
}
