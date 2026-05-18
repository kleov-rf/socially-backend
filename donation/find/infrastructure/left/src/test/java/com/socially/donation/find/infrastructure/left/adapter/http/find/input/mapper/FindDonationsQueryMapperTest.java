package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import org.junit.jupiter.api.Test;

class FindDonationsQueryMapperTest {

  private final FindDonationsQueryMapper mapper = new FindDonationsQueryMapper();

  @Test
  void toQuery_should_use_default_size_when_size_is_null() {
    FindDonationsQuery query = mapper.toQuery(null, null, null, null, null, null);

    assertEquals(PaginationCriteria.DEFAULT_SIZE.value(), query.paginationCriteria().size());
  }

  @Test
  void toQuery_should_map_cursor() {
    FindDonationsQuery query =
        mapper.toQuery(
            "next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "school", null, null);

    assertEquals("next", query.paginationCriteria().cursor());
  }

  @Test
  void toQuery_should_map_size() {
    FindDonationsQuery query = mapper.toQuery("next", 10, "oldest_first", "school", null, null);

    assertEquals(10, query.paginationCriteria().size());
  }

  @Test
  void toQuery_should_map_order() {
    FindDonationsQuery query =
        mapper.toQuery(
            "next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "school", null, null);

    assertEquals(PageOrder.fromValue("oldest_first"), query.paginationCriteria().order());
  }

  @Test
  void toQuery_should_map_query() {
    FindDonationsQuery query =
        mapper.toQuery(
            "next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "school", null, null);

    assertEquals("school", query.filterCriteria().query());
  }

  @Test
  void toQuery_should_normalize_blank_query_to_null() {
    FindDonationsQuery query =
        mapper.toQuery(
            "next", PaginationCriteria.DEFAULT_SIZE.value(), "oldest_first", "   ", null, null);

    assertNull(query.filterCriteria().query());
  }

  @Test
  void toQuery_should_map_order_to_nearest_first() {
    FindDonationsQuery query =
        mapper.toQuery("next", 10, "nearest_first", "school", 40.4168, -3.7038);

    assertEquals(PageOrder.NEAREST_FIRST, query.paginationCriteria().order());
  }

  @Test
  void toQuery_should_map_proximity_reference_when_order_is_nearest_first() {
    FindDonationsQuery query =
        mapper.toQuery("next", 10, "nearest_first", "school", 40.4168, -3.7038);

    assertEquals(ProximityReference.from(40.4168, -3.7038), query.proximityReference());
  }

  @Test
  void toQuery_should_set_proximity_reference_to_null_when_order_is_not_nearest_first() {
    FindDonationsQuery query =
        mapper.toQuery("next", 10, "newest_first", "school", 40.4168, -3.7038);

    assertNull(query.proximityReference());
  }

  @Test
  void toQuery_should_throw_when_nearest_first_without_coordinates() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toQuery("next", 10, "nearest_first", "school", null, null));

    assertEquals(
        "latitude and longitude are required when order is nearest_first", exception.getMessage());
  }
}
