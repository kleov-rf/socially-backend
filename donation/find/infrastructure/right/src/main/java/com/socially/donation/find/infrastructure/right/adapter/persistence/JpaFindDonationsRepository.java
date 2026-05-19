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
import java.util.Objects;
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
      ProximityReference proximityReference) {
    String searchPattern = searchPatternNormalizer.toSearchPattern(filterCriteria);

    KeysetCursor dateBoundary = null;
    ProximityKeysetCursor proximityBoundary = null;
    if (Objects.nonNull(paginationCriteria.cursor())) {
      if (paginationCriteria.order() == PageOrder.NEAREST_FIRST) {
        proximityBoundary = proximityCursorCodec.decode(paginationCriteria.cursor());
      } else {
        dateBoundary = cursorCodec.decode(paginationCriteria.cursor());
      }
    }

    boolean isPreviousCursorRequest =
        Objects.nonNull(paginationCriteria.cursor()) && isPreviousCursor(paginationCriteria);

    List<DonationEntity> entities =
        entityPageFetcher.fetch(
            new FetchCriteria(
                paginationCriteria,
                searchPattern,
                dateBoundary,
                proximityBoundary,
                proximityReference,
                isPreviousCursorRequest,
                PageRequest.of(0, paginationCriteria.size() + 1)));

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

    long totalCount;
    if (Objects.isNull(searchPattern)) {
      totalCount = entityRepository.countByDeletedAtIsNull();
    } else {
      totalCount = entityRepository.countBySearchPattern(searchPattern);
    }

    Metadata metadata =
        Metadata.create(
            cursorMetadata.nextCursor(),
            cursorMetadata.previousCursor(),
            paginationCriteria.size(),
            totalCount);

    List<Donation> donations = pageSlice.entities().stream().map(entityMapper::toDomain).toList();

    return Page.create(donations, metadata);
  }

  private boolean isPreviousCursor(PaginationCriteria paginationCriteria) {
    if (paginationCriteria.order() == PageOrder.NEAREST_FIRST) {
      return proximityCursorCodec.isPreviousCursor(paginationCriteria.cursor());
    }
    return cursorCodec.isPreviousCursor(paginationCriteria.cursor());
  }
}
