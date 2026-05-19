package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import org.springframework.stereotype.Component;

@Component
public class HaversineDistanceCalculator {
  private static final double EARTH_RADIUS_METERS = 6_371_000.0;

  public double distanceMeters(
      double referenceLatitude, double referenceLongitude, DonationEntity entity) {
    return distanceMeters(
        referenceLatitude,
        referenceLongitude,
        entity.getLocationLatitude(),
        entity.getLocationLongitude());
  }

  public double distanceMeters(
      double referenceLatitude, double referenceLongitude, double latitude, double longitude) {
    double referenceLatitudeRadians = Math.toRadians(referenceLatitude);
    double latitudeRadians = Math.toRadians(latitude);
    double longitudeRadians = Math.toRadians(longitude);
    double referenceLongitudeRadians = Math.toRadians(referenceLongitude);

    double cosineValue =
        Math.cos(referenceLatitudeRadians)
                * Math.cos(latitudeRadians)
                * Math.cos(longitudeRadians - referenceLongitudeRadians)
            + Math.sin(referenceLatitudeRadians) * Math.sin(latitudeRadians);
    cosineValue = Math.max(-1.0, Math.min(1.0, cosineValue));

    return EARTH_RADIUS_METERS * Math.acos(cosineValue);
  }
}
