package com.socially.donation.find.domain.pagination;

import java.util.Arrays;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PageSize {
  FIVE_ITEMS(5),
  TEN_ITEMS(10),
  TWENTY_ITEMS(20);

  private final int value;

  public int value() {
    return value;
  }

  public static PageSize fromValue(Integer value) {
    if (Objects.isNull(value)) {
      throw new IllegalArgumentException("Size must be one of: 5, 10, 20");
    }
    return Arrays.stream(values())
        .filter(size -> size.value == value)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Size must be one of: 5, 10, 20"));
  }
}
