package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
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
  public static final KeysetCursor BOUNDARY =
      new KeysetCursor(
          Instant.parse("2024-06-01T12:00:00Z"),
          Id.from("550e8400-e29b-41d4-a716-446655440000").value());
  public static final List<DonationEntity> DONATION_ENTITIES =
      List.of(
          DonationEntity.create(
              Id.from("550e8400-e29b-41d4-a716-446655440000").value(),
              Id.from(DONOR_ID).value(),
              "Donation Title",
              "Donation Description",
              Instant.parse("2024-06-01T12:00:00Z"),
              Instant.parse("2024-06-01T12:00:00Z")),
          DonationEntity.create(
              Id.from("550e8400-e29b-41d4-a716-446655440001").value(),
              Id.from(DONOR_ID).value(),
              "Another Donation Title",
              "Another Donation Description",
              Instant.parse("2024-06-02T12:00:00Z"),
              Instant.parse("2024-06-02T12:00:00Z")));

  @Mock private DonationEntityRepository entityRepository;

  @InjectMocks private DonationEntityPageFetcher sut;

  @Test
  void find_should_call_desc_when_no_cursor_newest_and_search_pattern_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST),
            SEARCH_PATTERN,
            null,
            false,
            PAGE_REQUEST);
    when(entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
            SEARCH_PATTERN, PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    var actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findBySearchPatternOrderByCreatedAtDescIdDesc(SEARCH_PATTERN, PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_asc_when_no_cursor_oldest_and_search_pattern_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST),
            SEARCH_PATTERN,
            null,
            false,
            PAGE_REQUEST);
    when(entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(SEARCH_PATTERN, PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findBySearchPatternOrderByCreatedAtAscIdAsc(SEARCH_PATTERN, PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_desc_when_no_cursor_newest_and_search_pattern_not_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST),
            null,
            null,
            false,
            PAGE_REQUEST);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository).findByOrderByCreatedAtDescIdDesc(PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_asc_when_no_cursor_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST),
            null,
            null,
            false,
            PAGE_REQUEST);
    when(entityRepository.findByOrderByCreatedAtAscIdAsc(PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository).findByOrderByCreatedAtAscIdAsc(PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_oldest_search_pattern_when_previous_request_oldest_and_search_pattern_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST),
            SEARCH_PATTERN,
            BOUNDARY,
            true,
            PAGE_REQUEST);
    when(entityRepository.findPreviousPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_newest_when_previous_request_newest_and_search_pattern_not_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST),
            null,
            BOUNDARY,
            true,
            PAGE_REQUEST);
    when(entityRepository.findPreviousPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository).findPreviousPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_oldest_when_previous_request_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST),
            null,
            BOUNDARY,
            true,
            PAGE_REQUEST);
    when(entityRepository.findPreviousPageForOldestFirst(
            BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousPageForOldestFirst(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_previous_newest_search_pattern_when_previous_request_newest_and_search_pattern_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST),
            SEARCH_PATTERN,
            BOUNDARY,
            true,
            PAGE_REQUEST);
    when(entityRepository.findPreviousPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findPreviousPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_next_newest_search_pattern_when_next_request_newest_and_search_pattern_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST),
            SEARCH_PATTERN,
            BOUNDARY,
            false,
            PAGE_REQUEST);
    when(entityRepository.findNextPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findNextPageBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void
      find_should_call_next_oldest_search_pattern_when_next_request_oldest_and_search_pattern_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST),
            SEARCH_PATTERN,
            BOUNDARY,
            false,
            PAGE_REQUEST);
    when(entityRepository.findNextPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findNextPageForOldestFirstBySearchPattern(
            SEARCH_PATTERN, BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_next_newest_when_next_request_newest_and_search_pattern_not_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST),
            null,
            BOUNDARY,
            false,
            PAGE_REQUEST);
    when(entityRepository.findNextPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository).findNextPage(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }

  @Test
  void find_should_call_next_oldest_when_next_request_oldest_and_search_pattern_not_present() {
    FetchCriteria criteria =
        new FetchCriteria(
            PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST),
            null,
            BOUNDARY,
            false,
            PAGE_REQUEST);
    when(entityRepository.findNextPageForOldestFirst(
            BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST))
        .thenReturn(DONATION_ENTITIES);

    List<DonationEntity> actualEntities = sut.fetch(criteria);

    verify(entityRepository)
        .findNextPageForOldestFirst(BOUNDARY.createdAt(), BOUNDARY.id(), PAGE_REQUEST);
    assertEquals(DONATION_ENTITIES, actualEntities);
  }
}
