package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.kernel.domain.valueobject.Id;
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

  @Mock private KeysetCursorCodec cursorCodec;

  private final CursorMetadataBuilder sut = new CursorMetadataBuilder();

  @Test
  void build_should_return_null_cursors_when_page_is_empty() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);

    CursorMetadata result = sut.build(List.of(), criteria, false, false, cursorCodec);

    assertNull(result.nextCursor());
    assertNull(result.previousCursor());
  }

  @Test
  void build_should_generate_next_cursor_when_overflow_exists_on_next_requests() {
    PaginationCriteria criteria =
        PaginationCriteria.create(null, PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");
    when(cursorCodec.encode(item.getCreatedAt(), item.getId())).thenReturn("next-cursor");

    CursorMetadata result = sut.build(List.of(item), criteria, false, true, cursorCodec);

    assertEquals("next-cursor", result.nextCursor());
    assertNull(result.previousCursor());
  }

  @Test
  void build_should_generate_previous_cursor_when_cursor_is_present_and_items_exist() {
    PaginationCriteria criteria =
        PaginationCriteria.create("cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");
    when(cursorCodec.encodePrevious(item.getCreatedAt(), item.getId()))
        .thenReturn("previous-cursor");

    CursorMetadata result = sut.build(List.of(item), criteria, false, false, cursorCodec);

    assertEquals("previous-cursor", result.previousCursor());
  }

  @Test
  void build_should_not_generate_previous_cursor_for_previous_requests_without_overflow() {
    PaginationCriteria criteria =
        PaginationCriteria.create(
            "previous-cursor", PageSize.FIVE_ITEMS, PaginationCriteria.DEFAULT_ORDER);
    DonationEntity item = entity("00");

    CursorMetadata result = sut.build(List.of(item), criteria, true, false, cursorCodec);

    assertNull(result.previousCursor());
    verify(cursorCodec, never()).encodePrevious(item.getCreatedAt(), item.getId());
  }

  private static DonationEntity entity(String suffix) {
    return DonationEntity.create(
        Id.from("550e8400-e29b-41d4-a716-4466554400" + suffix).value(),
        "Title",
        "Description",
        CREATED_AT,
        CREATED_AT);
  }
}
