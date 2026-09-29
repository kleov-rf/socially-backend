package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.find.domain.proximity.ProximityReference;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindDonationsRepository implements FindDonationsRepository {

  private final SearchPatternNormalizer searchPatternNormalizer;
  private final PageFetcher entityPageFetcher;
  private final PageSlicer pageSlicer;
  private final CursorMetadataBuilder cursorMetadataBuilder;
  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;
  private final KeysetCursorCodec cursorCodec;
  private final ProximityKeysetCursorCodec proximityCursorCodec;
  private final HaversineDistanceCalculator distanceCalculator;

  @Override
  public Page<Donation> find(
      PaginationCriteria paginationCriteria,
      FilterCriteria filterCriteria,
      Optional<ProximityReference> proximityReference) {
    Optional<String> searchPattern = searchPatternNormalizer.toSearchPattern(filterCriteria);

    Optional<KeysetCursor> dateBoundary = Optional.empty();
    Optional<ProximityKeysetCursor> proximityBoundary = Optional.empty();
    if (paginationCriteria.cursor().isPresent()) {
      String cursor = paginationCriteria.cursor().get();
      if (paginationCriteria.order() == PageOrder.NEAREST_FIRST) {
        proximityBoundary = Optional.of(proximityCursorCodec.decode(cursor));
      } else {
        dateBoundary = Optional.of(cursorCodec.decode(cursor));
      }
    }

    boolean isPreviousCursorRequest =
        paginationCriteria.cursor().isPresent() && isPreviousCursor(paginationCriteria);

    FetchCriteria fetchCriteria =
        FetchCriteria.create(
                paginationCriteria,
                isPreviousCursorRequest,
                PageRequest.of(0, paginationCriteria.size() + 1))
            .withSearchPattern(searchPattern)
            .withBoundary(dateBoundary)
            .withProximityBoundary(proximityBoundary)
            .withProximityReference(proximityReference);

    List<DonationEntity> entities = entityPageFetcher.fetch(fetchCriteria);

    PageSlice pageSlice =
        pageSlicer.slice(entities, paginationCriteria.size(), isPreviousCursorRequest);

    CursorMetadata cursorMetadata =
        cursorMetadataBuilder.build(
            pageSlice.entities(),
            paginationCriteria,
            proximityReference,
            isPreviousCursorRequest,
            pageSlice.overflowItemsExist(),
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    long totalCount =
        searchPattern
            .map(entityRepository::countBySearchPattern)
            .orElseGet(entityRepository::countByDeletedAtIsNull);

    Metadata metadata =
        Metadata.create(paginationCriteria.size(), totalCount)
            .withNextCursor(cursorMetadata.nextCursor())
            .withPreviousCursor(cursorMetadata.previousCursor());

    List<Donation> donations = pageSlice.entities().stream().map(entityMapper::toDomain).toList();

    return Page.create(donations, metadata);
  }

  private boolean isPreviousCursor(PaginationCriteria paginationCriteria) {
    return paginationCriteria
        .cursor()
        .map(
            cursor ->
                paginationCriteria.order() == PageOrder.NEAREST_FIRST
                    ? proximityCursorCodec.isPreviousCursor(cursor)
                    : cursorCodec.isPreviousCursor(cursor))
        .orElse(false);
  }
}
