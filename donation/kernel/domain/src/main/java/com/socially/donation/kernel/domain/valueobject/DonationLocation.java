package com.socially.donation.kernel.domain.valueobject;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class DonationLocation {
  private final String address;
  private final double latitude;
  private final double longitude;

  public static DonationLocation from(String address, Double latitude, Double longitude) {
    validate(address, latitude, longitude);
    return new DonationLocation(address.trim(), latitude, longitude);
  }

  private static void validate(String address, Double latitude, Double longitude) {
    if (address == null) {
      throw new IllegalArgumentException("donation location address cannot be null");
    }

    if (address.trim().isEmpty()) {
      throw new IllegalArgumentException("donation location address cannot be blank");
    }

    if (Objects.isNull(latitude)) {
      throw new IllegalArgumentException("donation location latitude cannot be null");
    }

    if (latitude < -90 || latitude > 90) {
      throw new IllegalArgumentException("donation location latitude must be between -90 and 90");
    }

    if (Objects.isNull(longitude)) {
      throw new IllegalArgumentException("donation location longitude cannot be null");
    }

    if (longitude < -180 || longitude > 180) {
      throw new IllegalArgumentException(
          "donation location longitude must be between -180 and 180");
    }
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
