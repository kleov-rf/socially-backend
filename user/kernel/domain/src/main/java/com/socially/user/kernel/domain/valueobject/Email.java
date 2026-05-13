package com.socially.user.kernel.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Email {
  private final String value;

  public static Email from(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("email cannot be blank");
    }
    return new Email(value.trim());
  }

  public String value() {
    return value;
  }
}
