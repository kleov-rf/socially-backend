package com.socially.donation.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DonationLocationTest {

  @Test
  void from_should_throw_exception_when_address_is_null() {
    assertThrows(
        IllegalArgumentException.class, () -> DonationLocation.from(null, 40.4168, -3.7038));
  }

  @Test
  void from_should_throw_exception_when_address_is_blank() {
    assertThrows(
        IllegalArgumentException.class, () -> DonationLocation.from("   ", 40.4168, -3.7038));
  }

  @Test
  void from_should_trim_address() {
    DonationLocation location = DonationLocation.from("  Calle Mayor 1  ", 40.4168, -3.7038);

    assertEquals("Calle Mayor 1", location.address());
  }

  @Test
  void from_should_throw_exception_when_latitude_is_below_minus_90() {
    assertThrows(
        IllegalArgumentException.class,
        () -> DonationLocation.from("Calle Mayor 1", -90.1, -3.7038));
  }

  @Test
  void from_should_throw_exception_when_latitude_is_above_90() {
    assertThrows(
        IllegalArgumentException.class,
        () -> DonationLocation.from("Calle Mayor 1", 90.1, -3.7038));
  }

  @Test
  void from_should_throw_exception_when_longitude_is_below_minus_180() {
    assertThrows(
        IllegalArgumentException.class,
        () -> DonationLocation.from("Calle Mayor 1", 40.4168, -180.1));
  }

  @Test
  void from_should_throw_exception_when_longitude_is_above_180() {
    assertThrows(
        IllegalArgumentException.class,
        () -> DonationLocation.from("Calle Mayor 1", 40.4168, 180.1));
  }

  @Test
  void from_should_create_location_on_happy_path() {
    DonationLocation location = DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038);

    assertEquals("Calle Mayor 1, Madrid", location.address());
    assertEquals(40.4168, location.latitude());
    assertEquals(-3.7038, location.longitude());
  }
}
