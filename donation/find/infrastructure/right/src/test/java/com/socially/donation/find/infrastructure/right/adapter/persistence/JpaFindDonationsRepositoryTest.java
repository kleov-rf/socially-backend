package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
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
  private static final ProximityReference PROXIMITY_REFERENCE =
      ProximityReference.from(40.4168, -3.7038);
  private static final ProximityKeysetCursor PROXIMITY_BOUNDARY =
      new ProximityKeysetCursor(1000.0, Id.from(DONATION_ID).value());

  @Mock private SearchPatternNormalizer searchPatternNormalizer;
  @Mock private PageFetcher entityPageFetcher;
  @Mock private PageSlicer pageSlicer;
  @Mock private CursorMetadataBuilder cursorMetadataBuilder;
  @Mock private DonationEntityRepository entityRepository;
  @Mock private DonationEntityMapper entityMapper;
  @Mock private KeysetCursorCodec cursorCodec;
  @Mock private ProximityKeysetCursorCodec proximityCursorCodec;
  @Mock private HaversineDistanceCalculator distanceCalculator;

  @InjectMocks private JpaFindDonationsRepository sut;

  @Test
  void find_should_call_unfiltered_count_when_search_pattern_is_null() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), false);

    sut.find(paginationCriteria, filterCriteria, null);

    verify(entityRepository).countByDeletedAtIsNull();
  }

  @Test
  void find_should_not_call_filtered_count_when_search_pattern_is_null() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), false);

    sut.find(paginationCriteria, filterCriteria, null);

    verify(entityRepository, never()).countBySearchPattern(any());
  }

  @Test
  void find_should_return_unfiltered_total_count_when_search_pattern_is_null() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    DonationEntity entity = donationEntity(DONATION_ID, "Title", "Description", CREATED_AT);
    Donation mappedDonation = mappedDonation(entity);
    stubFindPipeline(
        paginationCriteria, filterCriteria, null, List.of(entity), false, mappedDonation, 50L);

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria, null);

    assertEquals(50L, result.metadata().totalCount());
  }

  @Test
  void find_should_return_mapped_items_when_search_pattern_is_null() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    DonationEntity entity = donationEntity(DONATION_ID, "Title", "Description", CREATED_AT);
    Donation mappedDonation = mappedDonation(entity);
    stubFindPipeline(
        paginationCriteria, filterCriteria, null, List.of(entity), false, mappedDonation, 50L);

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria, null);

    assertEquals(List.of(mappedDonation), result.items());
  }

  @Test
  void find_should_call_filtered_count_when_search_pattern_is_present() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), false);

    sut.find(paginationCriteria, filterCriteria, null);

    verify(entityRepository).countBySearchPattern("%school%");
  }

  @Test
  void find_should_not_call_unfiltered_count_when_search_pattern_is_present() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), false);

    sut.find(paginationCriteria, filterCriteria, null);

    verify(entityRepository, never()).countByDeletedAtIsNull();
  }

  @Test
  void find_should_return_filtered_total_count_when_search_pattern_is_present() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), false, null, 10L);

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria, null);

    assertEquals(10L, result.metadata().totalCount());
  }

  @Test
  void find_should_decode_date_cursor_when_order_is_not_nearest_first() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(
            "previous-cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, Id.from(DONATION_ID).value());
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), true);
    when(cursorCodec.isPreviousCursor("previous-cursor")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor")).thenReturn(boundary);

    sut.find(paginationCriteria, filterCriteria, null);

    verify(cursorCodec).decode("previous-cursor");
  }

  @Test
  void find_should_propagate_date_boundary_to_fetcher() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(
            "previous-cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, Id.from(DONATION_ID).value());
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), true);
    when(cursorCodec.isPreviousCursor("previous-cursor")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor")).thenReturn(boundary);

    sut.find(paginationCriteria, filterCriteria, null);

    ArgumentCaptor<FetchCriteria> criteriaCaptor = ArgumentCaptor.forClass(FetchCriteria.class);
    verify(entityPageFetcher).fetch(criteriaCaptor.capture());
    assertEquals(boundary, criteriaCaptor.getValue().boundary());
  }

  @Test
  void find_should_propagate_previous_request_to_fetcher() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create(
            "previous-cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, Id.from(DONATION_ID).value());
    stubFindPipeline(paginationCriteria, filterCriteria, null, List.of(), true);
    when(cursorCodec.isPreviousCursor("previous-cursor")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor")).thenReturn(boundary);

    sut.find(paginationCriteria, filterCriteria, null);

    ArgumentCaptor<FetchCriteria> criteriaCaptor = ArgumentCaptor.forClass(FetchCriteria.class);
    verify(entityPageFetcher).fetch(criteriaCaptor.capture());
    assertEquals(true, criteriaCaptor.getValue().previousCursorRequest());
  }

  @Test
  void find_should_decode_proximity_cursor_when_order_is_nearest_first() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    stubFindPipeline(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE, List.of(), false);
    when(proximityCursorCodec.isPreviousCursor("cursor")).thenReturn(false);
    when(proximityCursorCodec.decode("cursor")).thenReturn(PROXIMITY_BOUNDARY);

    sut.find(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE);

    verify(proximityCursorCodec).decode("cursor");
  }

  @Test
  void find_should_not_decode_date_cursor_when_order_is_nearest_first() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    stubFindPipeline(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE, List.of(), false);
    when(proximityCursorCodec.isPreviousCursor("cursor")).thenReturn(false);
    when(proximityCursorCodec.decode("cursor")).thenReturn(PROXIMITY_BOUNDARY);

    sut.find(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE);

    verify(cursorCodec, never()).decode(any());
  }

  @Test
  void find_should_propagate_proximity_boundary_to_fetcher() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    stubFindPipeline(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE, List.of(), false);
    when(proximityCursorCodec.isPreviousCursor("cursor")).thenReturn(false);
    when(proximityCursorCodec.decode("cursor")).thenReturn(PROXIMITY_BOUNDARY);

    sut.find(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE);

    ArgumentCaptor<FetchCriteria> criteriaCaptor = ArgumentCaptor.forClass(FetchCriteria.class);
    verify(entityPageFetcher).fetch(criteriaCaptor.capture());
    assertEquals(PROXIMITY_BOUNDARY, criteriaCaptor.getValue().proximityBoundary());
  }

  @Test
  void find_should_propagate_proximity_reference_to_fetcher() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    FilterCriteria filterCriteria = FilterCriteria.create(null);
    stubFindPipeline(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE, List.of(), false);
    when(proximityCursorCodec.isPreviousCursor("cursor")).thenReturn(false);
    when(proximityCursorCodec.decode("cursor")).thenReturn(PROXIMITY_BOUNDARY);

    sut.find(paginationCriteria, filterCriteria, PROXIMITY_REFERENCE);

    ArgumentCaptor<FetchCriteria> criteriaCaptor = ArgumentCaptor.forClass(FetchCriteria.class);
    verify(entityPageFetcher).fetch(criteriaCaptor.capture());
    assertEquals(PROXIMITY_REFERENCE, criteriaCaptor.getValue().proximityReference());
  }

  @Test
  void find_should_return_next_cursor_from_metadata_builder() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    stubFindPipelineWithMetadata(
        paginationCriteria,
        filterCriteria,
        null,
        List.of(),
        false,
        new CursorMetadata("next", "previous"));

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria, null);

    assertEquals("next", result.metadata().nextCursor());
  }

  @Test
  void find_should_return_previous_cursor_from_metadata_builder() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    stubFindPipelineWithMetadata(
        paginationCriteria,
        filterCriteria,
        null,
        List.of(),
        false,
        new CursorMetadata("next", "previous"));

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria, null);

    assertEquals("previous", result.metadata().previousCursor());
  }

  @Test
  void find_should_return_empty_items_when_fetcher_returns_no_entities() {
    PaginationCriteria paginationCriteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    FilterCriteria filterCriteria = FilterCriteria.create("school");
    stubFindPipelineWithMetadata(
        paginationCriteria,
        filterCriteria,
        null,
        List.of(),
        false,
        new CursorMetadata("next", "previous"));

    Page<Donation> result = sut.find(paginationCriteria, filterCriteria, null);

    assertEquals(List.of(), result.items());
  }

  private void stubFindPipeline(
      PaginationCriteria paginationCriteria,
      FilterCriteria filterCriteria,
      ProximityReference proximityReference,
      List<DonationEntity> entities,
      boolean previousCursorRequest) {
    stubFindPipeline(
        paginationCriteria,
        filterCriteria,
        proximityReference,
        entities,
        previousCursorRequest,
        null,
        0L);
  }

  private void stubFindPipeline(
      PaginationCriteria paginationCriteria,
      FilterCriteria filterCriteria,
      ProximityReference proximityReference,
      List<DonationEntity> entities,
      boolean previousCursorRequest,
      Donation mappedDonation,
      long totalCount) {
    String searchPattern =
        filterCriteria.query() == null ? null : "%" + filterCriteria.query() + "%";
    when(searchPatternNormalizer.toSearchPattern(filterCriteria)).thenReturn(searchPattern);
    if (searchPattern == null) {
      when(entityRepository.countByDeletedAtIsNull()).thenReturn(totalCount);
    } else {
      when(entityRepository.countBySearchPattern(searchPattern)).thenReturn(totalCount);
    }
    when(entityPageFetcher.fetch(any())).thenReturn(entities);
    when(pageSlicer.slice(entities, paginationCriteria.size(), previousCursorRequest))
        .thenReturn(new PageSlice(entities, false));
    when(cursorMetadataBuilder.build(
            entities,
            paginationCriteria,
            proximityReference,
            previousCursorRequest,
            false,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator))
        .thenReturn(new CursorMetadata(null, null));
    if (mappedDonation != null && !entities.isEmpty()) {
      when(entityMapper.toDomain(entities.getFirst())).thenReturn(mappedDonation);
    }
  }

  private void stubFindPipelineWithMetadata(
      PaginationCriteria paginationCriteria,
      FilterCriteria filterCriteria,
      ProximityReference proximityReference,
      List<DonationEntity> entities,
      boolean previousCursorRequest,
      CursorMetadata cursorMetadata) {
    when(searchPatternNormalizer.toSearchPattern(filterCriteria)).thenReturn("%school%");
    when(entityRepository.countBySearchPattern("%school%")).thenReturn(100L);
    when(cursorCodec.isPreviousCursor("cursor")).thenReturn(false);
    when(cursorCodec.decode("cursor"))
        .thenReturn(new KeysetCursor(CREATED_AT, Id.from(DONATION_ID).value()));
    when(entityPageFetcher.fetch(any())).thenReturn(entities);
    when(pageSlicer.slice(entities, paginationCriteria.size(), previousCursorRequest))
        .thenReturn(new PageSlice(entities, false));
    when(cursorMetadataBuilder.build(
            entities,
            paginationCriteria,
            proximityReference,
            previousCursorRequest,
            false,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator))
        .thenReturn(cursorMetadata);
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
