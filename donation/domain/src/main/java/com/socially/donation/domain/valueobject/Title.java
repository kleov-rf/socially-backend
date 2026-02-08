package com.socially.donation.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Title {
  private final String value;

  public static Title from(String value) {
    return new Title(value);
  }

  public String value() {
    return value;
  }
}
