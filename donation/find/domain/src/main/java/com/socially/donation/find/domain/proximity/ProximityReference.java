package com.socially.donation.find.domain.proximity;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ProximityReference {
  private final double latitude;
  private final double longitude;

  public static ProximityReference from(Double latitude, Double longitude) {
    validate(latitude, longitude);
    return new ProximityReference(latitude, longitude);
  }

  private static void validate(Double latitude, Double longitude) {
    if (Objects.isNull(latitude)) {
      throw new IllegalArgumentException("proximity reference latitude cannot be null");
    }

    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("proximity reference latitude must be between -90 and 90");
    }

    if (Objects.isNull(longitude)) {
      throw new IllegalArgumentException("proximity reference longitude cannot be null");
    }

    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException(
          "proximity reference longitude must be between -180 and 180");
    }
  }

  public double latitude() {
    return latitude;
  }

  public double longitude() {
    return longitude;
  }
}
