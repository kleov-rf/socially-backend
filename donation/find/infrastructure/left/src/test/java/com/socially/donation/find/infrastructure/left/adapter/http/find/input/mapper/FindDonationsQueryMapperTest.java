package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationsQueryMapperTest {

  @InjectMocks FindDonationsQueryMapper mapper;

  @Test
  void toQuery_should_use_default_size_when_size_is_null() {
    FindDonationsQuery query = mapper.toQuery(null, null, null, null);

    assertEquals(PaginationCriteria.DEFAULT_SIZE.value(), query.paginationCriteria().size());
  }

  @Test
  void toQuery_should_map_cursor() {
    FindDonationsQuery query =
        mapper.toQuery("next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "school");

    assertEquals("next", query.paginationCriteria().cursor());
  }

  @Test
  void toQuery_should_map_size() {
    FindDonationsQuery query = mapper.toQuery("next", 10, "oldest_first", "school");

    assertEquals(10, query.paginationCriteria().size());
  }

  @Test
  void toQuery_should_map_order() {
    FindDonationsQuery query =
        mapper.toQuery("next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "school");

    assertEquals(PageOrder.fromValue("oldest_first"), query.paginationCriteria().order());
  }

  @Test
  void toQuery_should_map_query() {
    FindDonationsQuery query =
        mapper.toQuery("next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "school");

    assertEquals("school", query.filterCriteria().query());
  }

  @Test
  void toQuery_should_normalize_blank_query_to_null() {
    FindDonationsQuery query =
        mapper.toQuery("next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "   ");

    assertNull(query.filterCriteria().query());
  }
}
