package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class CursorMetadataBuilder {
  public CursorMetadata build(
      List<DonationEntity> pageEntities,
      PaginationCriteria paginationCriteria,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      KeysetCursorCodec cursorCodec) {
    String nextCursor =
        buildNextCursor(
            pageEntities,
            paginationCriteria,
            previousCursorRequest,
            overflowItemsExist,
            cursorCodec);
    String previousCursor =
        buildPreviousCursor(
            pageEntities,
            paginationCriteria,
            previousCursorRequest,
            overflowItemsExist,
            cursorCodec);
    return new CursorMetadata(nextCursor, previousCursor);
  }

  private String buildNextCursor(
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

  private String buildPreviousCursor(
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
}
