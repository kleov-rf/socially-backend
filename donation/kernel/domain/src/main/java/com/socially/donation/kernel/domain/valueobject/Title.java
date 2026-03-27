package com.socially.donation.kernel.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Title {
  private final String value;

  public static Title from(String value) {
    if (value == null) {
      throw new IllegalArgumentException("title cannot be null");
    }

    String normalizedValue = value.trim();
    if (normalizedValue.isEmpty()) {
      throw new IllegalArgumentException("title cannot be blank");
    }

    return new Title(normalizedValue);
  }

  public String value() {
    return value;
  }
}
