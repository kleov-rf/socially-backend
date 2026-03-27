package com.socially.donation.kernel.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Description {
  private final String value;

  public static Description from(String value) {
    if (value == null) {
      throw new IllegalArgumentException("description cannot be null");
    }

    String normalizedValue = value.trim();
    if (normalizedValue.isEmpty()) {
      throw new IllegalArgumentException("description cannot be blank");
    }

    return new Description(normalizedValue);
  }

  public String value() {
    return value;
  }
}
