package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CursorMetadataBuilder {

  public CursorMetadata build(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      Optional<ProximityReference> proximityReference,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec dateCursorCodec,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    if (paginationCriteria.order() == PageOrder.NEAREST_FIRST) {
      return buildProximityCursors(
          pageEntities,
          paginationCriteria,
          proximityReference,
          previousCursorRequest,
          overflowItemsExist,
          proximityCursorCodec,
          distanceCalculator);
    }

    return buildDateCursors(
        pageEntities,
        paginationCriteria,
        previousCursorRequest,
        overflowItemsExist,
        dateCursorCodec);
  }

  private CursorMetadata buildDateCursors(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec cursorCodec) {
    Optional<String> nextCursor =
        buildDateNextCursor(
            pageEntities,
            paginationCriteria,
            previousCursorRequest,
            overflowItemsExist,
            cursorCodec);
    Optional<String> previousCursor =
        buildDatePreviousCursor(
            pageEntities,
            paginationCriteria,
            previousCursorRequest,
            overflowItemsExist,
            cursorCodec);
    return CursorMetadata.create().withNextCursor(nextCursor).withPreviousCursor(previousCursor);
  }

  private CursorMetadata buildProximityCursors(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      Optional<ProximityReference> proximityReference,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    Optional<String> nextCursor =
        buildProximityNextCursor(
            pageEntities,
            paginationCriteria,
            proximityReference,
            previousCursorRequest,
            overflowItemsExist,
            proximityCursorCodec,
            distanceCalculator);
    Optional<String> previousCursor =
        buildProximityPreviousCursor(
            pageEntities,
            paginationCriteria,
            proximityReference,
            previousCursorRequest,
            overflowItemsExist,
            proximityCursorCodec,
            distanceCalculator);
    return CursorMetadata.create().withNextCursor(nextCursor).withPreviousCursor(previousCursor);
  }

  private Optional<String> buildDateNextCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      boolean isPreviousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec cursorCodec) {
    if (pageEntities.isEmpty()) {
      return Optional.empty();
    }
    if (!isPreviousCursorRequest && !overflowItemsExist) {
      return Optional.empty();
    }
    if (isPreviousCursorRequest && paginationCriteria.cursor().isEmpty()) {
      return Optional.empty();
    }

    DonationEntity lastEntity = pageEntities.getLast();
    return Optional.of(cursorCodec.encode(lastEntity.getCreatedAt(), lastEntity.getId()));
  }

  private Optional<String> buildDatePreviousCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec cursorCodec) {
    if (paginationCriteria.cursor().isEmpty() || pageEntities.isEmpty()) {
      return Optional.empty();
    }
    if (previousCursorRequest && !overflowItemsExist) {
      return Optional.empty();
    }

    DonationEntity firstEntity = pageEntities.getFirst();
    return Optional.of(cursorCodec.encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId()));
  }

  private Optional<String> buildProximityNextCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      Optional<ProximityReference> proximityReference,
      boolean isPreviousCursorRequest,
      boolean overflowItemsExist,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    if (pageEntities.isEmpty()) {
      return Optional.empty();
    }
    if (!isPreviousCursorRequest && !overflowItemsExist) {
      return Optional.empty();
    }
    if (isPreviousCursorRequest && paginationCriteria.cursor().isEmpty()) {
      return Optional.empty();
    }
    if (proximityReference.isEmpty()) {
      return Optional.empty();
    }

    ProximityReference reference = proximityReference.get();
    DonationEntity lastEntity = pageEntities.getLast();
    double distanceMeters =
        distanceCalculator.distanceMeters(reference.latitude(), reference.longitude(), lastEntity);
    return Optional.of(proximityCursorCodec.encode(distanceMeters, lastEntity.getId()));
  }

  private Optional<String> buildProximityPreviousCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      Optional<ProximityReference> proximityReference,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    if (paginationCriteria.cursor().isEmpty() || pageEntities.isEmpty()) {
      return Optional.empty();
    }
    if (previousCursorRequest && !overflowItemsExist) {
      return Optional.empty();
    }
    if (proximityReference.isEmpty()) {
      return Optional.empty();
    }

    ProximityReference reference = proximityReference.get();
    DonationEntity firstEntity = pageEntities.getFirst();
    double distanceMeters =
        distanceCalculator.distanceMeters(reference.latitude(), reference.longitude(), firstEntity);
    return Optional.of(proximityCursorCodec.encodePrevious(distanceMeters, firstEntity.getId()));
  }
}
