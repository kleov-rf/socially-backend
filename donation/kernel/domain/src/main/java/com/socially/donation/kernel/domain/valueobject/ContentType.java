package com.socially.donation.kernel.domain.valueobject;

import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ContentType {

  private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

  private final String value;

  public static ContentType from(String value) {
    validate(value);
    return new ContentType(value.trim().toLowerCase());
  }

  private static void validate(String value) {
    if (value == null) {
      throw new IllegalArgumentException("content type cannot be null");
    }

    if (value.trim().isEmpty()) {
      throw new IllegalArgumentException("content type cannot be blank");
    }

    if (!ALLOWED_TYPES.contains(value.trim().toLowerCase())) {
      throw new InvalidDonationImageException("Unsupported content type: " + value);
    }
  }

  public String value() {
    return value;
  }
}
