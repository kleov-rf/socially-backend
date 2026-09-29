package com.socially.donation.find.domain.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void create_should_create_page_with_received_items() {
    Metadata metadata = Metadata.create(10, 100L).withNextCursor(Optional.of("next"));
    Page<String> page = Page.create(List.of("a", "b"), metadata);

    assertEquals(List.of("a", "b"), page.items());
  }

  @Test
  void create_should_create_page_with_received_metadata() {
    Metadata metadata = Metadata.create(10, 100L).withNextCursor(Optional.of("next"));
    Page<String> page = Page.create(List.of("a", "b"), metadata);

    assertEquals(metadata, page.metadata());
  }
}
