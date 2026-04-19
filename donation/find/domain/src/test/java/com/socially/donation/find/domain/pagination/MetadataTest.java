package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MetadataTest {

  @Test
  void create_should_throw_exception_if_received_size_is_equal_or_less_than_zero() {
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(null, false, 0));
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(null, false, -1));
  }

  @Test
  void
      create_should_throw_exception_if_received_has_next_is_true_and_received_cursor_is_not_present() {
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(null, true, 10));
  }

  @Test
  void create_should_throw_exception_if_received_has_next_is_true_and_received_cursor_is_blank() {
    assertThrows(IllegalArgumentException.class, () -> Metadata.create(" ", true, 10));
  }

  @Test
  void create_should_return_received_cursor() {
    Metadata metadata = Metadata.create("next-cursor", true, 10);

    assertEquals("next-cursor", metadata.nextCursor());
  }

  @Test
  void create_should_return_null_if_received_cursor_is_blank() {
    Metadata metadata = Metadata.create("   ", false, 10);

    assertNull(metadata.nextCursor());
  }

  @Test
  void create_should_return_received_has_next() {
    Metadata metadata = Metadata.create("next-cursor", true, 10);

    assertTrue(metadata.hasNext());
  }

  @Test
  void create_should_return_received_size() {
    Metadata metadata = Metadata.create("next-cursor", true, 10);

    assertEquals(10, metadata.size());
  }
}
