package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class HaversineDistanceCalculatorTest {

  private static final double MADRID_LATITUDE = 40.4168;
  private static final double MADRID_LONGITUDE = -3.7038;
  private static final double LONDON_LATITUDE = 51.5074;
  private static final double LONDON_LONGITUDE = -0.1278;
  private static final double MADRID_TO_LONDON_METERS = 1_263_412.0;
  private static final double DISTANCE_TOLERANCE_METERS = 1_000.0;

  private final HaversineDistanceCalculator sut = new HaversineDistanceCalculator();

  @Test
  void distanceMeters_should_return_zero_when_coordinates_match() {
    double distance =
        sut.distanceMeters(MADRID_LATITUDE, MADRID_LONGITUDE, MADRID_LATITUDE, MADRID_LONGITUDE);

    assertEquals(0.0, distance, 0.1);
  }

  @Test
  void distanceMeters_should_return_expected_distance_between_madrid_and_london() {
    double distance =
        sut.distanceMeters(MADRID_LATITUDE, MADRID_LONGITUDE, LONDON_LATITUDE, LONDON_LONGITUDE);

    assertEquals(MADRID_TO_LONDON_METERS, distance, DISTANCE_TOLERANCE_METERS);
  }

  @Test
  void distanceMeters_should_delegate_to_coordinate_method_for_donation_entity() {
    DonationEntity entity =
        DonationEntity.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000").value(),
            Id.from("550e8400-e29b-41d4-a716-446655449999").value(),
            "Title",
            "Description",
            Instant.parse("2024-06-01T12:00:00Z"),
            Instant.parse("2024-06-01T12:00:00Z"),
            "Calle Mayor 1, Madrid",
            MADRID_LATITUDE,
            MADRID_LONGITUDE);

    double distance = sut.distanceMeters(MADRID_LATITUDE, MADRID_LONGITUDE, entity);

    assertEquals(0.0, distance, 0.1);
  }
}
