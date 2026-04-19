package com.socially.donation.find.application.input;

import com.socially.donation.find.domain.pagination.PaginationCriteria;

public record FindDonationsQuery(PaginationCriteria paginationCriteria) {}
