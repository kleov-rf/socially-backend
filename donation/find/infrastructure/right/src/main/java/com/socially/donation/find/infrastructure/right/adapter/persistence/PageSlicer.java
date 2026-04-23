package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PageSlicer {
  public PageSlice slice(
      List<DonationEntity> entities, int pageSize, boolean isPreviousCursorRequest) {
    if (entities.isEmpty()) {
      return new PageSlice(entities, false);
    }

    boolean overflowItemsExist = entities.size() > pageSize;

    if (isPreviousCursorRequest) {
      return processPreviousCursorRequest(entities, pageSize, overflowItemsExist);
    }

    if (overflowItemsExist) {
      return new PageSlice(entities.subList(0, pageSize), true);
    }
    return new PageSlice(entities, false);
  }

  private static PageSlice processPreviousCursorRequest(
      List<DonationEntity> entities, int pageSize, boolean overflowItemsExist) {
    List<DonationEntity> entitiesAscending = entities;
    if (overflowItemsExist) {
      entitiesAscending = entities.subList(0, pageSize);
    }

    List<DonationEntity> entitiesDescending = new ArrayList<>(entitiesAscending);
    Collections.reverse(entitiesDescending);
    return new PageSlice(entitiesDescending, overflowItemsExist);
  }
}
