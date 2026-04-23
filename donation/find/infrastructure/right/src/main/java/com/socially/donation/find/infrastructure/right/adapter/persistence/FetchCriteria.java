package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PaginationCriteria;
import org.springframework.data.domain.PageRequest;

public record FetchCriteria(
    PaginationCriteria paginationCriteria,
    String searchPattern,
    KeysetCursor boundary,
    boolean previousCursorRequest,
    PageRequest pageRequest) {}
