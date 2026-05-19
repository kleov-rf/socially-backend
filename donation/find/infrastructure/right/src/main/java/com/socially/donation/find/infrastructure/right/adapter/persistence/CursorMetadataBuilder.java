package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class CursorMetadataBuilder {
  public CursorMetadata build(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      ProximityReference proximityReference,
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
    String nextCursor =
        buildDateNextCursor(
            pageEntities,
            paginationCriteria,
            previousCursorRequest,
            overflowItemsExist,
            cursorCodec);
    String previousCursor =
        buildDatePreviousCursor(
            pageEntities,
            paginationCriteria,
            previousCursorRequest,
            overflowItemsExist,
            cursorCodec);
    return new CursorMetadata(nextCursor, previousCursor);
  }

  private CursorMetadata buildProximityCursors(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      ProximityReference proximityReference,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    String nextCursor =
        buildProximityNextCursor(
            pageEntities,
            paginationCriteria,
            proximityReference,
            previousCursorRequest,
            overflowItemsExist,
            proximityCursorCodec,
            distanceCalculator);
    String previousCursor =
        buildProximityPreviousCursor(
            pageEntities,
            paginationCriteria,
            proximityReference,
            previousCursorRequest,
            overflowItemsExist,
            proximityCursorCodec,
            distanceCalculator);
    return new CursorMetadata(nextCursor, previousCursor);
  }

  private String buildDateNextCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      boolean isPreviousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec cursorCodec) {
    if (pageEntities.isEmpty()) {
      return null;
    }
    if (!isPreviousCursorRequest && !overflowItemsExist) {
      return null;
    }
    if (isPreviousCursorRequest && Objects.isNull(paginationCriteria.cursor())) {
      return null;
    }

    DonationEntity lastEntity = pageEntities.getLast();
    return cursorCodec.encode(lastEntity.getCreatedAt(), lastEntity.getId());
  }

  private String buildDatePreviousCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec cursorCodec) {
    if (Objects.isNull(paginationCriteria.cursor()) || pageEntities.isEmpty()) {
      return null;
    }
    if (previousCursorRequest && !overflowItemsExist) {
      return null;
    }

    DonationEntity firstEntity = pageEntities.getFirst();
    return cursorCodec.encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId());
  }

  private String buildProximityNextCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      ProximityReference proximityReference,
      boolean isPreviousCursorRequest,
      boolean overflowItemsExist,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    if (pageEntities.isEmpty()) {
      return null;
    }
    if (!isPreviousCursorRequest && !overflowItemsExist) {
      return null;
    }
    if (isPreviousCursorRequest && Objects.isNull(paginationCriteria.cursor())) {
      return null;
    }

    DonationEntity lastEntity = pageEntities.getLast();
    double distanceMeters =
        distanceCalculator.distanceMeters(
            proximityReference.latitude(), proximityReference.longitude(), lastEntity);
    return proximityCursorCodec.encode(distanceMeters, lastEntity.getId());
  }

  private String buildProximityPreviousCursor(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      ProximityReference proximityReference,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      ProximityKeysetCursorCodec proximityCursorCodec,
      HaversineDistanceCalculator distanceCalculator) {
    if (Objects.isNull(paginationCriteria.cursor()) || pageEntities.isEmpty()) {
      return null;
    }
    if (previousCursorRequest && !overflowItemsExist) {
      return null;
    }

    DonationEntity firstEntity = pageEntities.getFirst();
    double distanceMeters =
        distanceCalculator.distanceMeters(
            proximityReference.latitude(), proximityReference.longitude(), firstEntity);
    return proximityCursorCodec.encodePrevious(distanceMeters, firstEntity.getId());
  }
}
