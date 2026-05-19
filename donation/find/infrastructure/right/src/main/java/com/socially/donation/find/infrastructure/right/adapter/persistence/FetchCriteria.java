package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import org.springframework.data.domain.PageRequest;

public record FetchCriteria(
    PaginationCriteria paginationCriteria,
    String searchPattern,
    KeysetCursor boundary,
    ProximityKeysetCursor proximityBoundary,
    ProximityReference proximityReference,
    boolean previousCursorRequest,
    PageRequest pageRequest) {}
