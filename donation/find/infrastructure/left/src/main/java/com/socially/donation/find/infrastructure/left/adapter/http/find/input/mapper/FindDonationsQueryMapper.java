package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.exception.FindDonationsBadRequestException;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FindDonationsQueryMapper {
  public FindDonationsQuery toQuery(
      String cursor, Integer size, String order, String query, Double latitude, Double longitude) {
    PageOrder resolvedOrder = PageOrder.fromValue(order);
    PageSize resolvedSize =
        size == null ? PaginationCriteria.DEFAULT_SIZE : PageSize.fromValue(size);
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(cursor, resolvedSize, resolvedOrder);

    FilterCriteria filterCriteria = FilterCriteria.create(query);

    ProximityReference proximityReference =
        resolveProximityReference(resolvedOrder, latitude, longitude);

    return new FindDonationsQuery(paginationCriteria, filterCriteria, proximityReference);
  }

  private ProximityReference resolveProximityReference(
      PageOrder order, Double latitude, Double longitude) {
    if (order != PageOrder.NEAREST_FIRST) {
      return null;
    }

    if (Objects.isNull(latitude) || Objects.isNull(longitude)) {
      throw new FindDonationsBadRequestException(
          "latitude and longitude are required when order is nearest_first");
    }

    return ProximityReference.from(latitude, longitude);
  }
}
