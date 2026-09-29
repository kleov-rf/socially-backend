package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.exception.FindDonationsBadRequestException;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FindDonationsQueryMapper {

  public FindDonationsQuery toQuery(
      Optional<String> cursor,
      Optional<Integer> size,
      Optional<String> order,
      Optional<String> query,
      Optional<Double> latitude,
      Optional<Double> longitude) {
    PageOrder resolvedOrder =
        order.map(PageOrder::fromValue).orElse(PaginationCriteria.DEFAULT_ORDER);
    PageSize resolvedSize = size.map(PageSize::fromValue).orElse(PaginationCriteria.DEFAULT_SIZE);
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(resolvedSize, resolvedOrder).withCursor(cursor);

    FilterCriteria filterCriteria = FilterCriteria.create().withQuery(query);

    Optional<ProximityReference> proximityReference =
        resolveProximityReference(resolvedOrder, latitude, longitude);

    return FindDonationsQuery.create(paginationCriteria, filterCriteria)
        .withProximityReference(proximityReference);
  }

  private Optional<ProximityReference> resolveProximityReference(
      PageOrder order, Optional<Double> latitude, Optional<Double> longitude) {
    if (order != PageOrder.NEAREST_FIRST) {
      return Optional.empty();
    }

    double lat =
        latitude.orElseThrow(
            () ->
                new FindDonationsBadRequestException(
                    "latitude and longitude are required when order is nearest_first"));
    double lon =
        longitude.orElseThrow(
            () ->
                new FindDonationsBadRequestException(
                    "latitude and longitude are required when order is nearest_first"));

    return Optional.of(ProximityReference.create(lat, lon));
  }
}
