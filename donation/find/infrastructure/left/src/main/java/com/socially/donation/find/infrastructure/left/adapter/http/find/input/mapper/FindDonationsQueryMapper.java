package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import org.springframework.stereotype.Component;

@Component
public class FindDonationsQueryMapper {
  public FindDonationsQuery toQuery(String cursor, Integer size, String order, String query) {
    PageOrder resolvedOrder = PageOrder.fromValue(order);
    PageSize resolvedSize =
        size == null ? PaginationCriteria.DEFAULT_SIZE : PageSize.fromValue(size);
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(cursor, resolvedSize, resolvedOrder);

    FilterCriteria filterCriteria = FilterCriteria.create(query);

    return new FindDonationsQuery(paginationCriteria, filterCriteria);
  }
}
