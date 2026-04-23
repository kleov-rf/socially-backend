package com.socially.donation.find.domain.pagination;

import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PageOrder {
  NEWEST_FIRST("newest_first"),
  OLDEST_FIRST("oldest_first");

  private final String value;

  public String value() {
    return value;
  }

  public static PageOrder fromValue(String value) {
    if (Objects.isNull(value)) {
      return NEWEST_FIRST;
    }

    return Arrays.stream(values())
        .filter(order -> order.value.equals(value))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException("Order must be one of: newest_first, oldest_first"));
  }
}
