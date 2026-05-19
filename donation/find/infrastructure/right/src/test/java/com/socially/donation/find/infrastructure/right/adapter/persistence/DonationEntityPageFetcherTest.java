package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DonationEntityPageFetcherTest {
  private static final String SEARCH_PATTERN = "%school%";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655449999";
  private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 6);
  private static final KeysetCursor BOUNDARY =
      new KeysetCursor(
          Instant.parse("2024-06-01T12:00:00Z"),
          Id.from("550e8400-e29b-41d4-a716-446655440000").value());
  private static final List<DonationEntity> DONATION_ENTITIES =
      List.of(
          DonationEntity.create(
              Id.from("550e8400-e29b-41d4-a716-446655440000").value(),
              Id.from(DONOR_ID).value(),
              "Donation Title",
              "Donation Description",
              Instant.parse("2024-06-01T12:00:00Z"),
              Instant.parse("2024-06-01T12:00:00Z"),
              "Calle Mayor 1, Madrid",
              40.4168,
              -3.7038),
          DonationEntity.create(
              Id.from("550e8400-e29b-41d4-a716-446655440001").value(),
              Id.from(DONOR_ID).value(),
              "Another Donation Title",
              "Another Donation Description",
              Instant.parse("2024-06-02T12:00:00Z"),
              Instant.parse("2024-06-02T12:00:00Z"),
              "Calle Mayor 1, Madrid",
              40.4168,
              -3.7038));
  private static final ProximityReference PROXIMITY_REFERENCE =
      ProximityReference.from(40.4168, -3.7038);
  private static final ProximityKeysetCursor PROXIMITY_BOUNDARY =
      new ProximityKeysetCursor(1000.0, Id.from("550e8400-e29b-41d4-a716-446655440000").value());

  @Mock private DonationEntityRepository entityRepository;

  @InjectMocks private DonationEntityPageFetcher sut;

  @Test
  void find_should_call_nearest_first_when_no_cursor_and_search_pattern_not_present() {
    FetchCriteria criteria = nearestInitialCriteria(null);
    when(entityRepository.findNearestFirst(
            PROXIMITY_REFERENCE.latitude(), PROXIMITY_REFERENCE.longitude(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNearestFirst(
            PROXIMITY_REFERENCE.latitude(), PROXIMITY_REFERENCE.longitude(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_nearest_first_when_no_cursor_and_search_pattern_not_present() {
    FetchCriteria criteria = nearestInitialCriteria(null);
    when(entityRepository.findNearestFirst(
            PROXIMITY_REFERENCE.latitude(), PROXIMITY_REFERENCE.longitude(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_nearest_first_by_search_pattern_when_no_cursor_and_search_pattern_present() {
    FetchCriteria criteria = nearestInitialCriteria(SEARCH_PATTERN);
    when(entityRepository.findNearestFirstBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNearestFirstBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_nearest_first_by_search_pattern_when_no_cursor_and_search_pattern_present() {
    FetchCriteria criteria = nearestInitialCriteria(SEARCH_PATTERN);
    when(entityRepository.findNearestFirstBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_next_nearest_first_when_next_request_and_search_pattern_not_present() {
    FetchCriteria criteria = nearestNextCriteria(null);
    when(entityRepository.findNextNearestFirstPage(
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNextNearestFirstPage(
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_next_nearest_first_when_next_request_and_search_pattern_not_present() {
    FetchCriteria criteria = nearestNextCriteria(null);
    when(entityRepository.findNextNearestFirstPage(
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_next_nearest_first_by_search_pattern_when_next_request_and_search_pattern_present() {
    FetchCriteria criteria = nearestNextCriteria(SEARCH_PATTERN);
    when(entityRepository.findNextNearestFirstPageBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNextNearestFirstPageBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_next_nearest_first_by_search_pattern_when_next_request_and_search_pattern_present() {
    FetchCriteria criteria = nearestNextCriteria(SEARCH_PATTERN);
    when(entityRepository.findNextNearestFirstPageBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_nearest_first_when_previous_request_and_search_pattern_not_present() {
    FetchCriteria criteria = nearestPreviousCriteria(null);
    when(entityRepository.findPreviousNearestFirstPage(
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousNearestFirstPage(
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_previous_nearest_first_when_previous_request_and_search_pattern_not_present() {
    FetchCriteria criteria = nearestPreviousCriteria(null);
    when(entityRepository.findPreviousNearestFirstPage(
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_nearest_first_by_search_pattern_when_previous_request_and_search_pattern_present() {
    FetchCriteria criteria = nearestPreviousCriteria(SEARCH_PATTERN);
    when(entityRepository.findPreviousNearestFirstPageBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousNearestFirstPageBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_previous_nearest_first_by_search_pattern_when_previous_request_and_search_pattern_present() {
    FetchCriteria criteria = nearestPreviousCriteria(SEARCH_PATTERN);
    when(entityRepository.findPreviousNearestFirstPageBySearchPattern(
            SEARCH_PATTERN,
            PROXIMITY_REFERENCE.latitude(),
            PROXIMITY_REFERENCE.longitude(),
            PROXIMITY_BOUNDARY.distanceMeters(),
            PROXIMITY_BOUNDARY.id(),
            PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_desc_when_no_cursor_newest_and_search_pattern_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.NEWEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
            SEARCH_PATTERN, PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findBySearchPatternOrderByCreatedAtDescIdDesc(SEARCH_PATTERN, PAGE_REQUEST);
  }

  @Test
  void find_should_return_entities_from_desc_when_no_cursor_newest_and_search_pattern_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.NEWEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
            SEARCH_PATTERN, PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_asc_when_no_cursor_oldest_and_search_pattern_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.OLDEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(SEARCH_PATTERN, PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findBySearchPatternOrderByCreatedAtAscIdAsc(SEARCH_PATTERN, PAGE_REQUEST);
  }

  @Test
  void find_should_return_entities_from_asc_when_no_cursor_oldest_and_search_pattern_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.OLDEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(SEARCH_PATTERN, PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_desc_when_no_cursor_newest_and_search_pattern_not_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.NEWEST_FIRST, null);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository).findByOrderByCreatedAtDescIdDesc(PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_desc_when_no_cursor_newest_and_search_pattern_not_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.NEWEST_FIRST, null);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_asc_when_no_cursor_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.OLDEST_FIRST, null);
    when(entityRepository.findByOrderByCreatedAtAscIdAsc(PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository).findByOrderByCreatedAtAscIdAsc(PAGE_REQUEST);
  }

  @Test
  void find_should_return_entities_from_asc_when_no_cursor_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria = initialCriteria(PageOrder.OLDEST_FIRST, null);
    when(entityRepository.findByOrderByCreatedAtAscIdAsc(PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_oldest_search_pattern_when_previous_request_oldest_and_search_pattern_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.OLDEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findPreviousPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_previous_oldest_search_pattern_when_previous_request_oldest_and_search_pattern_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.OLDEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findPreviousPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_newest_when_previous_request_newest_and_search_pattern_not_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.NEWEST_FIRST, null);
    when(entityRepository.findPreviousPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository).findPreviousPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_previous_newest_when_previous_request_newest_and_search_pattern_not_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.NEWEST_FIRST, null);
    when(entityRepository.findPreviousPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_oldest_when_previous_request_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.OLDEST_FIRST, null);
    when(entityRepository.findPreviousPageForOldestFirst(
            BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousPageForOldestFirst(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_previous_oldest_when_previous_request_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.OLDEST_FIRST, null);
    when(entityRepository.findPreviousPageForOldestFirst(
            BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_newest_search_pattern_when_previous_request_newest_and_search_pattern_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.NEWEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findPreviousPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_previous_newest_search_pattern_when_previous_request_newest_and_search_pattern_present() {
    FetchCriteria criteria = previousCriteria(PageOrder.NEWEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findPreviousPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_next_newest_search_pattern_when_next_request_newest_and_search_pattern_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.NEWEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findNextPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNextPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_next_newest_search_pattern_when_next_request_newest_and_search_pattern_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.NEWEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findNextPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_next_oldest_search_pattern_when_next_request_oldest_and_search_pattern_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.OLDEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findNextPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNextPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_next_oldest_search_pattern_when_next_request_oldest_and_search_pattern_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.OLDEST_FIRST, SEARCH_PATTERN);
    when(entityRepository.findNextPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_next_newest_when_next_request_newest_and_search_pattern_not_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.NEWEST_FIRST, null);
    when(entityRepository.findNextPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository).findNextPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_next_newest_when_next_request_newest_and_search_pattern_not_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.NEWEST_FIRST, null);
    when(entityRepository.findNextPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_next_oldest_when_next_request_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.OLDEST_FIRST, null);
    when(entityRepository.findNextPageForOldestFirst(
            BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    sut.fetch(criteria);

    verify(entityRepository)
        .findNextPageForOldestFirst(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
  }

  @Test
  void
      find_should_return_entities_from_next_oldest_when_next_request_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria = nextCriteria(PageOrder.OLDEST_FIRST, null);
    when(entityRepository.findNextPageForOldestFirst(
            BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  private static FetchCriteria nearestInitialCriteria(String searchPattern) {
    return new FetchCriteria(
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST),
        searchPattern,
        null,
        null,
        PROXIMITY_REFERENCE,
        false,
        PAGE_REQUEST);
  }

  private static FetchCriteria nearestNextCriteria(String searchPattern) {
    return new FetchCriteria(
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST),
        searchPattern,
        null,
        PROXIMITY_BOUNDARY,
        PROXIMITY_REFERENCE,
        false,
        PAGE_REQUEST);
  }

  private static FetchCriteria nearestPreviousCriteria(String searchPattern) {
    return new FetchCriteria(
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST),
        searchPattern,
        null,
        PROXIMITY_BOUNDARY,
        PROXIMITY_REFERENCE,
        true,
        PAGE_REQUEST);
  }

  private static FetchCriteria initialCriteria(PageOrder order, String searchPattern) {
    return new FetchCriteria(
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, order),
        searchPattern,
        null,
        null,
        null,
        false,
        PAGE_REQUEST);
  }

  private static FetchCriteria previousCriteria(PageOrder order, String searchPattern) {
    return new FetchCriteria(
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, order),
        searchPattern,
        BOUNDARY,
        null,
        null,
        true,
        PAGE_REQUEST);
  }

  private static FetchCriteria nextCriteria(PageOrder order, String searchPattern) {
    return new FetchCriteria(
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, order),
        searchPattern,
        BOUNDARY,
        null,
        null,
        false,
        PAGE_REQUEST);
  }
}
