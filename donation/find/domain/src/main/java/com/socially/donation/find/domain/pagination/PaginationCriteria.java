package com.socially.donation.find.domain.pagination;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class PaginationCriteria {
  public static final PageSize DEFAULT_SIZE = PageSize.FIVE_ITEMS;
  public static final PageOrder DEFAULT_ORDER = PageOrder.NEWEST_FIRST;

  private final String cursor;
  private final PageSize size;
  private final PageOrder order;

  public static PaginationCriteria create(String cursor, PageSize size, PageOrder order) {
    if (Objects.nonNull(cursor) && cursor.isBlank()) {
      return new PaginationCriteria(null, size, order);
    }
    return new PaginationCriteria(cursor, size, order);
  }

  public String cursor() {
    return cursor;
  }

  public int size() {
    return size.value();
  }

  public PageOrder order() {
    return order;
  }
}
