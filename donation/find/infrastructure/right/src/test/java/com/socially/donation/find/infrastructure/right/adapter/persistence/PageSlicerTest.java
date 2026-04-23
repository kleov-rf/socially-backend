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
  void slice_should_return_slice_with_received_entities_when_entities_empty() {
    List<DonationEntity> entities = List.of();

    PageSlice result = sut.slice(entities, 5, false);

    assertEquals(entities, result.entities());
  }

  @Test
  void slice_should_return_slice_with_overflow_not_exists_when_entities_empty() {
    PageSlice result = sut.slice(List.of(), 5, false);

    assertFalse(result.overflowItemsExist());
  }

  @Test
  void slice_should_return_slice_with_overflow_exists_when_entities_size_greater_than_page_size() {
    List<DonationEntity> entities = List.of(entity("1"), entity("2"), entity("3"));

    PageSlice result = sut.slice(entities, 2, false);

    assertTrue(result.overflowItemsExist());
  }

  @Test
  void slice_should_return_slice_with_overflow_not_exists_when_entities_size_equal_to_page_size() {
    List<DonationEntity> entities = List.of(entity("1"), entity("2"));

    PageSlice result = sut.slice(entities, 2, false);

    assertFalse(result.overflowItemsExist());
  }

  @Test
  void
      slice_should_return_slice_with_overflow_not_exists_when_entities_size_lesser_than_page_size() {
    List<DonationEntity> entities = List.of(entity("1"), entity("2"));

    PageSlice result = sut.slice(entities, 3, false);

    assertFalse(result.overflowItemsExist());
  }

  @Test
  void slice_should_return_slice_with_entities_of_page_size_when_overflow_exists() {
    List<DonationEntity> entities = List.of(entity("1"), entity("2"), entity("3"));

    PageSlice result = sut.slice(entities, 2, false);

    assertEquals(List.of(entities.get(0), entities.get(1)), result.entities());
  }

  @Test
  void slice_should_return_slice_with_received_entities_when_overflow_not_exists() {
    List<DonationEntity> entities = List.of(entity("1"), entity("2"));

    PageSlice result = sut.slice(entities, 2, false);

    assertEquals(entities, result.entities());
  }

  @Test
  void slice_should_return_slice_with_reversed_entities_when_is_previous_cursor_request() {
    List<DonationEntity> entitiesAscending = List.of(entity("1"), entity("2"), entity("3"));

    PageSlice result = sut.slice(entitiesAscending, 3, true);

    assertEquals(
        List.of(entitiesAscending.get(2), entitiesAscending.get(1), entitiesAscending.get(0)),
        result.entities());
  }

  @Test
  void
      slice_should_return_slice_with_entities_of_page_size_when_overflow_exists_for_previous_cursor_request() {
    List<DonationEntity> entitiesAscending =
        List.of(entity("1"), entity("2"), entity("3"), entity("4"));

    PageSlice result = sut.slice(entitiesAscending, 3, true);

    assertEquals(
        List.of(entitiesAscending.get(2), entitiesAscending.get(1), entitiesAscending.get(0)),
        result.entities());
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
