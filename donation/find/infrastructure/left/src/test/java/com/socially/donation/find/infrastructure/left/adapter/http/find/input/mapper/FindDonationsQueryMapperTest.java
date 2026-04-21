package com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FindDonationsQueryMapperTest {

  private final FindDonationsQueryMapper mapper = new FindDonationsQueryMapper();

  @Test
  void toQuery_should_use_default_size_when_size_is_null() {
    var query = mapper.toQuery(null, null);

    assertEquals(5, query.paginationCriteria().size());
    assertNull(query.paginationCriteria().cursor());
  }

  @Test
  void toQuery_should_map_cursor_and_size() {
    var query = mapper.toQuery("next", 5);

    assertEquals("next", query.paginationCriteria().cursor());
    assertEquals(5, query.paginationCriteria().size());
  }
}
