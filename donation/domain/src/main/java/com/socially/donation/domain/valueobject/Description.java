package com.socially.donation.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Description {
  private final String value;

  public static Description from(String value) {
    return new Description(value);
  }

  public String value() {
    return value;
  }
}
