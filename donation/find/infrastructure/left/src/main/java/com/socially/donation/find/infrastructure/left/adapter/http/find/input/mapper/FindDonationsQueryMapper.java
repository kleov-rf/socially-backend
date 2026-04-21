package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.pagination.DonationsOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class FindDonationsQueryMapper {
  public FindDonationsQuery toQuery(String cursor, Integer size, String order) {
    PaginationCriteria paginationCriteria;
    DonationsOrder resolvedOrder = DonationsOrder.fromValue(order);

    if (Objects.nonNull(size)) {
      paginationCriteria = PaginationCriteria.create(cursor, size, resolvedOrder);
    } else {
      paginationCriteria =
          PaginationCriteria.create(cursor, PaginationCriteria.DEFAULT_SIZE, resolvedOrder);
    }

    return new FindDonationsQuery(paginationCriteria);
  }
}
