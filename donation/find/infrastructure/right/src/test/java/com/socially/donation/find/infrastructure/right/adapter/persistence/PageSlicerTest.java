package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PageSlicerTest {
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private final PageSlicer sut = new PageSlicer();

  @Test
  void slice_should_return_empty_slice_when_no_items() {
    PageSlice result = sut.slice(List.of(), 5, false);

    assertEquals(List.of(), result.entities());
    assertFalse(result.overflowItemsExist());
  }

  @Test
  void slice_should_trim_overflow_for_next_page() {
    List<DonationEntity> items = List.of(entity("1"), entity("2"), entity("3"));

    PageSlice result = sut.slice(items, 2, false);

    assertEquals(items.getFirst(), result.entities().getFirst());
    assertEquals(items.get(1), result.entities().get(1));
    assertTrue(result.overflowItemsExist());
  }

  @Test
  void slice_should_reverse_items_for_previous_page() {
    List<DonationEntity> ascendingItems = List.of(entity("1"), entity("2"), entity("3"));

    PageSlice result = sut.slice(ascendingItems, 3, true);

    assertEquals(ascendingItems.get(2), result.entities().getFirst());
    assertEquals(ascendingItems.get(1), result.entities().get(1));
    assertEquals(ascendingItems.getFirst(), result.entities().get(2));
    assertFalse(result.overflowItemsExist());
  }

  private static DonationEntity entity(String sequence) {
    return DonationEntity.create(
        Id.from(String.format("550e8400-e29b-41d4-a716-4466554400%02d", Integer.parseInt(sequence)))
            .value(),
        "Title " + sequence,
        "Description " + sequence,
        CREATED_AT,
        CREATED_AT);
  }
}
