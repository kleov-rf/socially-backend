package com.socially.donation.find.domain.port.right;

import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.kernel.domain.entity.Donation;

public interface FindDonationsRepository {
  Page<Donation> find(PaginationCriteria paginationCriteria);
}
