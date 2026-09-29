package com.socially.donation.find.application.input;

import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FindDonationsQuery(
    PaginationCriteria paginationCriteria,
    FilterCriteria filterCriteria,
    Optional<ProximityReference> proximityReference) {

  public static FindDonationsQuery create(
      PaginationCriteria paginationCriteria, FilterCriteria filterCriteria) {
    return new FindDonationsQuery(paginationCriteria, filterCriteria, Optional.empty());
  }

  public FindDonationsQuery withProximityReference(
      Optional<ProximityReference> proximityReference) {
    return new FindDonationsQuery(paginationCriteria, filterCriteria, proximityReference);
  }
}
