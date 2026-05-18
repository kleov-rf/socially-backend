package com.socially.donation.find.application.input;

import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;

public record FindDonationsQuery(
    PaginationCriteria paginationCriteria,
    FilterCriteria filterCriteria,
    ProximityReference proximityReference) {}
