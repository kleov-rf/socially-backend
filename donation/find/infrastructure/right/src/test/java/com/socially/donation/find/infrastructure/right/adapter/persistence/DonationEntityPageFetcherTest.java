package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
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
  @Mock private DonationEntityRepository entityRepository;
  @InjectMocks private DonationEntityPageFetcher sut;

  @Test
  void fetch_should_call_initial_newest_query_when_cursor_absent_and_no_search() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST);
    PageRequest pageRequest = PageRequest.of(0, 6);
    FetchCriteria fetchCriteria = new FetchCriteria(criteria, null, null, false, pageRequest);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(pageRequest)).thenReturn(List.of());

    sut.fetch(fetchCriteria);

    verify(entityRepository).findByOrderByCreatedAtDescIdDesc(pageRequest);
  }

  @Test
  void fetch_should_call_next_search_query_when_cursor_is_next_and_search_present() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEWEST_FIRST);
    PageRequest pageRequest = PageRequest.of(0, 6);
    KeysetCursor boundary =
        new KeysetCursor(
            Instant.parse("2024-06-01T12:00:00Z"),
            Id.from("550e8400-e29b-41d4-a716-446655440000").value());
    FetchCriteria fetchCriteria =
        new FetchCriteria(criteria, "%school%", boundary, false, pageRequest);
    when(entityRepository.findNextPageBySearchPattern(
            "%school%", boundary.createdAt(), boundary.id(), pageRequest))
        .thenReturn(List.of());

    sut.fetch(fetchCriteria);

    verify(entityRepository)
        .findNextPageBySearchPattern("%school%", boundary.createdAt(), boundary.id(), pageRequest);
  }

  @Test
  void fetch_should_call_previous_oldest_search_query_when_previous_and_oldest() {
    PaginationCriteria criteria =
        PaginationCriteria.create("previous-cursor", PageSize.FIVE_ITEMS, PageOrder.OLDEST_FIRST);
    PageRequest pageRequest = PageRequest.of(0, 6);
    KeysetCursor boundary =
        new KeysetCursor(
            Instant.parse("2024-06-01T12:00:00Z"),
            Id.from("550e8400-e29b-41d4-a716-446655440000").value());
    FetchCriteria fetchCriteria =
        new FetchCriteria(criteria, "%school%", boundary, true, pageRequest);
    when(entityRepository.findPreviousPageForOldestFirstBySearchPattern(
            "%school%", boundary.createdAt(), boundary.id(), pageRequest))
        .thenReturn(List.of());

    sut.fetch(fetchCriteria);

    verify(entityRepository)
        .findPreviousPageForOldestFirstBySearchPattern(
            "%school%", boundary.createdAt(), boundary.id(), pageRequest);
  }
}
