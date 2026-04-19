package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void create_should_throw_exception_if_received_items_is_null() {
    Metadata metadata = Metadata.create(null, null, 10);

    assertThrows(IllegalArgumentException.class, () -> Page.create(null, metadata));
  }

  @Test
  void create_should_throw_exception_if_received_metadata_is_null() {
    assertThrows(IllegalArgumentException.class, () -> Page.create(List.of(), null));
  }

  @Test
  void create_should_create_page_with_received_items() {
    Metadata metadata = Metadata.create("next", null, 10);
    Page<String> page = Page.create(List.of("a", "b"), metadata);

    assertEquals(List.of("a", "b"), page.items());
  }

  @Test
  void create_should_create_page_with_received_metadata() {
    Metadata metadata = Metadata.create("next", null, 10);
    Page<String> page = Page.create(List.of("a", "b"), metadata);

    assertEquals(metadata, page.metadata());
  }
}
