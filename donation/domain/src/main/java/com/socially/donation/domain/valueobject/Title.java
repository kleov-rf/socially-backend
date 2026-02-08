package com.socially.donation.domain.valueobject;

import java.util.Objects;
import lombok.RequiredArgsConstructor;

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
