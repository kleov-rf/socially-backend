package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFindDonationsRepositoryTest {
  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final DonationLocation DEFAULT_LOCATION =
      DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  @Mock private SearchPatternNormalizer searchPatternNormalizer;
  @Mock private PageFetcher entityPageFetcher;
  @Mock private PageSlicer pageSlicer;
  @Mock private CursorMetadataBuilder cursorMetadataBuilder;
  @Mock private DonationEntityRepository entityRepository;
  @Mock private DonationEntityMapper entityMapper;
  @Mock private KeysetCursorCodec cursorCodec;

  @InjectMocks private JpaFindDonationsRepository sut;

  @Test
  void find_should_use_unfiltered_total_count_when_search_pattern_is_null() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    DonationEntity entity = donationEntity(DONATION_ID, "Title", "Description", CREATED_AT);
    Donation mappedDonation = mappedDonation(entity);
    when(searchPatternNormalizer.toSearchPattern(filterCriteria)).thenReturn(null);
    when(entityRepository.countByDeletedAtIsNull()).thenReturn(50L);
    when(entityPageFetcher.fetch(any())).thenReturn(List.of(entity));
    when(pageSlicer.slice(List.of(entity), paginationCriteria.size(), false))
        .thenReturn(new PageSlice(List.of(entity), false));
    when(cursorMetadataBuilder.build(
            List.of(entity), paginationCriteria, false, false, cursorCodec))
        .thenReturn(new CursorMetadata(null, null));
    when(entityMapper.toDomain(entity)).thenReturn(mappedDonation);

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria);

    verify(entityRepository).countByDeletedAtIsNull();
    verify(entityRepository, never()).countBySearchPattern(any());
    assertEquals(50L, result.metadata().totalCount());
    assertEquals(List.of(mappedDonation), result.items());
  }

  @Test
  void find_should_use_filtered_total_count_when_search_pattern_is_present() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    when(searchPatternNormalizer.toSearchPattern(filterCriteria)).thenReturn("%school%");
    when(entityRepository.countBySearchPattern("%school%")).thenReturn(10L);
    when(entityPageFetcher.fetch(any())).thenReturn(List.of());
    when(pageSlicer.slice(List.of(), paginationCriteria.size(), false))
        .thenReturn(new PageSlice(List.of(), false));
    when(cursorMetadataBuilder.build(List.of(), paginationCriteria, false, false, cursorCodec))
        .thenReturn(new CursorMetadata(null, null));

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria);

    verify(entityRepository).countBySearchPattern("%school%");
    verify(entityRepository, never()).countByDeletedAtIsNull();
    assertEquals(10L, result.metadata().totalCount());
  }

  @Test
  void find_should_decode_cursor_and_propagate_previous_request_to_fetcher() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(
            "previous-cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, Id.from(DONATION_ID).value());
    when(searchPatternNormalizer.toSearchPattern(filterCriteria)).thenReturn(null);
    when(entityRepository.countByDeletedAtIsNull()).thenReturn(0L);
    when(cursorCodec.isPreviousCursor("previous-cursor")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor")).thenReturn(boundary);
    when(entityPageFetcher.fetch(any())).thenReturn(List.of());
    when(pageSlicer.slice(List.of(), paginationCriteria.size(), true))
        .thenReturn(new PageSlice(List.of(), false));
    when(cursorMetadataBuilder.build(List.of(), paginationCriteria, true, false, cursorCodec))
        .thenReturn(new CursorMetadata(null, null));

    sut.find(paginationCriteria, filterCriteria);

    ArgumentCaptor<FetchCriteria> criteriaCaptor = ArgumentCaptor.forClass(FetchCriteria.class);
    verify(entityPageFetcher).fetch(criteriaCaptor.capture());
    assertEquals(boundary, criteriaCaptor.getValue().boundary());
    assertEquals(true, criteriaCaptor.getValue().previousCursorRequest());
    verify(cursorCodec).decode("previous-cursor");
  }

  @Test
  void find_should_return_metadata_from_cursor_builder() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    when(searchPatternNormalizer.toSearchPattern(filterCriteria)).thenReturn("%school%");
    when(entityRepository.countBySearchPattern("%school%")).thenReturn(100L);
    when(cursorCodec.isPreviousCursor("cursor")).thenReturn(false);
    when(cursorCodec.decode("cursor"))
        .thenReturn(new KeysetCursor(CREATED_AT, Id.from(DONATION_ID).value()));
    when(entityPageFetcher.fetch(any())).thenReturn(List.of());
    when(pageSlicer.slice(List.of(), paginationCriteria.size(), false))
        .thenReturn(new PageSlice(List.of(), false));
    when(cursorMetadataBuilder.build(List.of(), paginationCriteria, false, false, cursorCodec))
        .thenReturn(new CursorMetadata("next", "previous"));

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria);

    assertEquals("next", result.metadata().nextCursor());
    assertEquals("previous", result.metadata().previousCursor());
    assertEquals(List.of(), result.items());
  }

  private static DonationEntity donationEntity(
      String id, String title, String description, Instant createdAt) {
    return DonationEntity.create(
        Id.from(id).value(),
        Id.from(DONOR_ID).value(),
        title,
        description,
        createdAt,
        createdAt,
        DEFAULT_LOCATION.address(),
        DEFAULT_LOCATION.latitude(),
        DEFAULT_LOCATION.longitude());
  }

  private static Donation mappedDonation(DonationEntity entity) {
    return Donation.create(
        Id.from(entity.getId().toString()),
        Id.from(entity.getDonorId().toString()),
        Title.from(entity.getTitle()),
        Description.from(entity.getDescription()),
        DEFAULT_LOCATION,
        entity.getCreatedAt(),
        entity.getLastUpdatedAt());
  }
}
