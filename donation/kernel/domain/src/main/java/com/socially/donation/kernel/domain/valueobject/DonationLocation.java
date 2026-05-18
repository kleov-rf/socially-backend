package com.socially.donation.kernel.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class DonationLocation {
  private final String address;
  private final double latitude;
  private final double longitude;

  public static DonationLocation from(String address, double latitude, double longitude) {
    if (address == null) {
      throw new IllegalArgumentException("donation location address cannot be null");
    }

    String normalizedAddress = address.trim();
    if (normalizedAddress.isEmpty()) {
      throw new IllegalArgumentException("donation location address cannot be blank");
    }

    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("donation location latitude must be between -90 and 90");
    }

    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException(
          "donation location longitude must be between -180 and 180");
    }

    return new DonationLocation(normalizedAddress, latitude, longitude);
  }

  public String address() {
    return address;
  }

  public double latitude() {
    return latitude;
  }

  public double longitude() {
    return longitude;
  }
}
