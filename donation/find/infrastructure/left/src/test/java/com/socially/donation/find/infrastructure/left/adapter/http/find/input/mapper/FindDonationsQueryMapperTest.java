package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.domain.exception.FindDonationsBadRequestException;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FindDonationsQueryMapperTest {

  private final FindDonationsQueryMapper mapper = new FindDonationsQueryMapper();

  @Test
  void toQuery_should_use_default_size_when_size_is_empty() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    assertEquals(PaginationCriteria.DEFAULT_SIZE.value(), query.paginationCriteria().size());
  }

  @Test
  void toQuery_should_map_cursor() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(PaginationCriteria.DEFAULT_SIZE.value()),
            Optional.of("oldest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());

    assertEquals(Optional.of("next"), query.paginationCriteria().cursor());
  }

  @Test
  void toQuery_should_map_size() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(10),
            Optional.of("oldest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());

    assertEquals(10, query.paginationCriteria().size());
  }

  @Test
  void toQuery_should_map_order() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(PaginationCriteria.DEFAULT_SIZE.value()),
            Optional.of("oldest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());

    assertEquals(PageOrder.fromValue("oldest_first"), query.paginationCriteria().order());
  }

  @Test
  void toQuery_should_map_query() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(PaginationCriteria.DEFAULT_SIZE.value()),
            Optional.of("oldest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());

    assertEquals(Optional.of("school"), query.filterCriteria().query());
  }

  @Test
  void toQuery_should_normalize_blank_query_to_empty() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(PaginationCriteria.DEFAULT_SIZE.value()),
            Optional.of("oldest_first"),
            Optional.of("   "),
            Optional.empty(),
            Optional.empty());

    assertEquals(Optional.empty(), query.filterCriteria().query());
  }

  @Test
  void toQuery_should_map_order_to_nearest_first() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(10),
            Optional.of("nearest_first"),
            Optional.of("school"),
            Optional.of(40.4168),
            Optional.of(-3.7038));

    assertEquals(PageOrder.NEAREST_FIRST, query.paginationCriteria().order());
  }

  @Test
  void toQuery_should_map_proximity_reference_when_order_is_nearest_first() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(10),
            Optional.of("nearest_first"),
            Optional.of("school"),
            Optional.of(40.4168),
            Optional.of(-3.7038));

    assertEquals(
        Optional.of(ProximityReference.create(40.4168, -3.7038)), query.proximityReference());
  }

  @Test
  void toQuery_should_set_proximity_reference_to_empty_when_order_is_not_nearest_first() {
    FindDonationsQuery query =
        mapper.toQuery(
            Optional.of("next"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.of(40.4168),
            Optional.of(-3.7038));

    assertEquals(Optional.empty(), query.proximityReference());
  }

  @Test
  void toQuery_should_throw_when_nearest_first_without_coordinates() {
    FindDonationsBadRequestException exception =
        assertThrows(
            FindDonationsBadRequestException.class,
            () ->
                mapper.toQuery(
                    Optional.of("next"),
                    Optional.of(10),
                    Optional.of("nearest_first"),
                    Optional.of("school"),
                    Optional.empty(),
                    Optional.empty()));

    assertEquals(
        "latitude and longitude are required when order is nearest_first", exception.getMessage());
  }
}
