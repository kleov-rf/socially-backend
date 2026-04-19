package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MetadataTest {

  @Test
  void create_should_throw_exception_if_received_size_is_equal_or_less_than_zero() {
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(null, null, 0));
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(null, null, -1));
  }

  @Test
  void create_should_return_received_cursor() {
    Metadata metadata = Metadata.create("next-cursor", "previous-cursor", 10);

    assertEquals("next-cursor", metadata.nextCursor());
  }

  @Test
  void create_should_return_null_if_received_cursor_is_blank() {
    Metadata metadata = Metadata.create("   ", "previous-cursor", 10);

    assertNull(metadata.nextCursor());
  }

  @Test
  void create_should_return_received_previous_cursor() {
    Metadata metadata = Metadata.create("next-cursor", "previous-cursor", 10);

    assertEquals("previous-cursor", metadata.previousCursor());
  }

  @Test
  void create_should_return_null_if_received_previous_cursor_is_blank() {
    Metadata metadata = Metadata.create("next-cursor", "   ", 10);

    assertNull(metadata.previousCursor());
  }

  @Test
  void create_should_return_true_for_has_next_when_next_cursor_is_present() {
    Metadata metadata = Metadata.create("next-cursor", "previous-cursor", 10);

    assertTrue(metadata.hasNext());
  }

  @Test
  void create_should_return_false_for_has_next_when_next_cursor_is_absent() {
    Metadata metadata = Metadata.create(null, "previous-cursor", 10);

    assertFalse(metadata.hasNext());
  }

  @Test
  void create_should_return_true_for_has_previous_when_previous_cursor_is_present() {
    Metadata metadata = Metadata.create("next-cursor", "previous-cursor", 10);

    assertTrue(metadata.hasPrevious());
  }

  @Test
  void create_should_return_false_for_has_previous_when_previous_cursor_is_absent() {
    Metadata metadata = Metadata.create("next-cursor", null, 10);

    assertFalse(metadata.hasPrevious());
  }

  @Test
  void create_should_return_received_size() {
    Metadata metadata = Metadata.create("next-cursor", "previous-cursor", 10);

    assertEquals(10, metadata.size());
  }
}
