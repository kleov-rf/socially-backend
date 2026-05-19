package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.proximity.ProximityReference;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CursorMetadataBuilderTest {
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655449999";
  private static final ProximityReference PROXIMITY_REFERENCE =
      ProximityReference.from(40.4168, -3.7038);

  @Mock private KeysetCursorCodec cursorCodec;
  @Mock private ProximityKeysetCursorCodec proximityCursorCodec;
  @Mock private HaversineDistanceCalculator distanceCalculator;

  private final CursorMetadataBuilder sut = new CursorMetadataBuilder();

  @Test
  void build_should_return_null_next_cursor_when_page_is_empty() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    CursorMetadata result =
        sut.build(
            List.of(),
            criteria,
            null,
            false,
            false,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertNull(result.nextCursor());
  }

  @Test
  void build_should_return_null_previous_cursor_when_page_is_empty() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    CursorMetadata result =
        sut.build(
            List.of(),
            criteria,
            null,
            false,
            false,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertNull(result.previousCursor());
  }

  @Test
  void build_should_generate_next_cursor_when_overflow_exists_on_next_requests() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");
    when(cursorCodec.encode(item.getCreatedAt(), item.getId())).thenReturn("next-cursor");

    CursorMetadata result =
        sut.build(
            List.of(item),
            criteria,
            null,
            false,
            true,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertEquals("next-cursor", result.nextCursor());
  }

  @Test
  void build_should_return_null_previous_cursor_when_overflow_exists_on_next_requests() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");
    when(cursorCodec.encode(item.getCreatedAt(), item.getId())).thenReturn("next-cursor");

    CursorMetadata result =
        sut.build(
            List.of(item),
            criteria,
            null,
            false,
            true,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertNull(result.previousCursor());
  }

  @Test
  void build_should_generate_previous_cursor_when_cursor_is_present_and_items_exist() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");
    when(cursorCodec.encodePrevious(item.getCreatedAt(), item.getId()))
        .thenReturn("previous-cursor");

    CursorMetadata result =
        sut.build(
            List.of(item),
            criteria,
            null,
            false,
            false,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertEquals("previous-cursor", result.previousCursor());
  }

  @Test
  void build_should_not_generate_previous_cursor_for_previous_requests_without_overflow() {
    PaginationCriteria criteria =
        PaginationCriteria.create(
            "previous-cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");

    sut.build(
        List.of(item),
        criteria,
        null,
        true,
        false,
        cursorCodec,
        proximityCursorCodec,
        distanceCalculator);

    verify(cursorCodec, never()).encodePrevious(item.getCreatedAt(), item.getId());
  }

  @Test
  void build_should_generate_proximity_next_cursor_when_order_is_nearest_first() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    DonationEntity item = entity("00");
    when(distanceCalculator.distanceMeters(
            PROXIMITY_REFERENCE.latitude(), PROXIMITY_REFERENCE.longitude(), item))
        .thenReturn(1234.5);
    when(proximityCursorCodec.encode(1234.5, item.getId())).thenReturn("proximity-next");

    CursorMetadata result =
        sut.build(
            List.of(item),
            criteria,
            PROXIMITY_REFERENCE,
            false,
            true,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertEquals("proximity-next", result.nextCursor());
  }

  @Test
  void build_should_call_proximity_codec_encode_when_order_is_nearest_first() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    DonationEntity item = entity("00");
    when(distanceCalculator.distanceMeters(
            PROXIMITY_REFERENCE.latitude(), PROXIMITY_REFERENCE.longitude(), item))
        .thenReturn(1234.5);
    when(proximityCursorCodec.encode(1234.5, item.getId())).thenReturn("proximity-next");

    sut.build(
        List.of(item),
        criteria,
        PROXIMITY_REFERENCE,
        false,
        true,
        cursorCodec,
        proximityCursorCodec,
        distanceCalculator);

    verify(proximityCursorCodec).encode(1234.5, item.getId());
  }

  @Test
  void build_should_generate_proximity_previous_cursor_when_order_is_nearest_first() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PageOrder.NEAREST_FIRST);
    DonationEntity item = entity("00");
    when(distanceCalculator.distanceMeters(
            PROXIMITY_REFERENCE.latitude(), PROXIMITY_REFERENCE.longitude(), item))
        .thenReturn(1234.5);
    when(proximityCursorCodec.encodePrevious(1234.5, item.getId()))
        .thenReturn("proximity-previous");

    CursorMetadata result =
        sut.build(
            List.of(item),
            criteria,
            PROXIMITY_REFERENCE,
            false,
            false,
            cursorCodec,
            proximityCursorCodec,
            distanceCalculator);

    assertEquals("proximity-previous", result.previousCursor());
  }

  private static DonationEntity entity(String suffix) {
    return DonationEntity.create(
        Id.from("550e8400-e29b-41d4-a716-4466554400" + suffix).value(),
        Id.from(DONOR_ID).value(),
        "Title",
        "Description",
        CREATED_AT,
        CREATED_AT,
        "Calle Mayor 1, Madrid",
        40.4168,
        -3.7038);
  }
}
