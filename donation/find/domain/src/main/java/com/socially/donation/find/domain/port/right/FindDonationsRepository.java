package com.socially.donation.find.domain.port.right;

import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import com.socially.donation.kernel.domain.entity.Donation;
import java.util.Optional;

public interface FindDonationsRepository {
  Page<Donation> find(
      PaginationCriteria paginationCriteria,
      FilterCriteria filterCriteria,
      Optional<ProximityReference> proximityReference);
}
