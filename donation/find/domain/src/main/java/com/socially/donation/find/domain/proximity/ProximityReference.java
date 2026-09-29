package com.socially.donation.find.domain.proximity;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record ProximityReference(double latitude, double longitude) {

  public static ProximityReference create(double latitude, double longitude) {
    validate(latitude, longitude);
    return new ProximityReference(latitude, longitude);
  }

  private static void validate(double latitude, double longitude) {
    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("proximity reference latitude must be between -90 and 90");
    }

    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException(
          "proximity reference longitude must be between -180 and 180");
    }
  }
}
