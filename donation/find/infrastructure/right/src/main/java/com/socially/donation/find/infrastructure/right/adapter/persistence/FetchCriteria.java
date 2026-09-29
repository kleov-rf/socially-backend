package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

public record FetchCriteria(
    PaginationCriteria paginationCriteria,
    boolean previousCursorRequest,
    PageRequest pageRequest,
    Optional<String> searchPattern,
    Optional<KeysetCursor> boundary,
    Optional<ProximityKeysetCursor> proximityBoundary,
    Optional<ProximityReference> proximityReference) {

  public static FetchCriteria create(
      PaginationCriteria paginationCriteria,
      boolean previousCursorRequest,
      PageRequest pageRequest) {
    return new FetchCriteria(
        paginationCriteria,
        previousCursorRequest,
        pageRequest,
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  public FetchCriteria withSearchPattern(Optional<String> searchPattern) {
    return new FetchCriteria(
        paginationCriteria,
        previousCursorRequest,
        pageRequest,
        searchPattern,
        boundary,
        proximityBoundary,
        proximityReference);
  }

  public FetchCriteria withBoundary(Optional<KeysetCursor> boundary) {
    return new FetchCriteria(
        paginationCriteria,
        previousCursorRequest,
        pageRequest,
        searchPattern,
        boundary,
        proximityBoundary,
        proximityReference);
  }

  public FetchCriteria withProximityBoundary(Optional<ProximityKeysetCursor> proximityBoundary) {
    return new FetchCriteria(
        paginationCriteria,
        previousCursorRequest,
        pageRequest,
        searchPattern,
        boundary,
        proximityBoundary,
        proximityReference);
  }

  public FetchCriteria withProximityReference(Optional<ProximityReference> proximityReference) {
    return new FetchCriteria(
        paginationCriteria,
        previousCursorRequest,
        pageRequest,
        searchPattern,
        boundary,
        proximityBoundary,
        proximityReference);
  }
}
