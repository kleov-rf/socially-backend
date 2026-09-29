package com.socially.donation.find.domain.pagination;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PaginationCriteria(PageSize pageSize, PageOrder order, Optional<String> cursor) {
  public static final PageSize DEFAULT_SIZE = PageSize.FIVE_ITEMS;
  public static final PageOrder DEFAULT_ORDER = PageOrder.NEWEST_FIRST;

  public static PaginationCriteria create(PageSize size, PageOrder order) {
    return new PaginationCriteria(size, order, Optional.empty());
  }

  public PaginationCriteria withCursor(Optional<String> cursor) {
    return new PaginationCriteria(pageSize, order, normalizeCursor(cursor));
  }

  private static Optional<String> normalizeCursor(Optional<String> cursor) {
    return cursor.map(String::trim).filter(s -> !s.isBlank());
  }

  public int size() {
    return pageSize.value();
  }
}
