package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class MetadataTest {

  @Test
  void create_should_throw_exception_if_received_size_is_equal_or_less_than_zero() {
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(0, 0L));
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(-1, 0L));
  }

  @Test
  void create_should_throw_exception_if_received_total_count_is_less_than_zero() {
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(10, -1L));
  }

  @Test
  void create_should_return_received_cursor() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertEquals(Optional.of("next-cursor"), metadata.nextCursor());
  }

  @Test
  void create_should_return_empty_if_received_cursor_is_blank() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("   "))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertEquals(Optional.empty(), metadata.nextCursor());
  }

  @Test
  void create_should_return_received_previous_cursor() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertEquals(Optional.of("previous-cursor"), metadata.previousCursor());
  }

  @Test
  void create_should_return_empty_if_received_previous_cursor_is_blank() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("   "));

    assertEquals(Optional.empty(), metadata.previousCursor());
  }

  @Test
  void create_should_return_true_for_has_next_when_next_cursor_is_present() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertTrue(metadata.hasNext());
  }

  @Test
  void create_should_return_false_for_has_next_when_next_cursor_is_absent() {
    Metadata metadata =
        Metadata.create(10, 100L).withPreviousCursor(Optional.of("previous-cursor"));

    assertFalse(metadata.hasNext());
  }

  @Test
  void create_should_return_true_for_has_previous_when_previous_cursor_is_present() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertTrue(metadata.hasPrevious());
  }

  @Test
  void create_should_return_false_for_has_previous_when_previous_cursor_is_absent() {
    Metadata metadata = Metadata.create(10, 100L).withNextCursor(Optional.of("next-cursor"));

    assertFalse(metadata.hasPrevious());
  }

  @Test
  void create_should_return_received_size() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertEquals(10, metadata.size());
  }

  @Test
  void create_should_return_received_total_count() {
    Metadata metadata =
        Metadata.create(10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor"));

    assertEquals(100L, metadata.totalCount());
  }
}
