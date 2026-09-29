package com.socially.donation.find.domain.proximity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ProximityReferenceTest {

  @Test
  void create_should_set_latitude_and_longitude_on_happy_path() {
    ProximityReference reference = ProximityReference.create(40.4168, -3.7038);

    assertEquals(40.4168, reference.latitude());
    assertEquals(-3.7038, reference.longitude());
  }

  @Test
  void create_should_throw_when_latitude_is_below_minus_90() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> ProximityReference.create(-90.1, -3.7038));

    assertEquals("proximity reference latitude must be between -90 and 90", exception.getMessage());
  }

  @Test
  void create_should_throw_when_latitude_is_above_90() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> ProximityReference.create(90.1, -3.7038));

    assertEquals("proximity reference latitude must be between -90 and 90", exception.getMessage());
  }

  @Test
  void create_should_throw_when_longitude_is_below_minus_180() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> ProximityReference.create(40.4168, -180.1));

    assertEquals(
        "proximity reference longitude must be between -180 and 180", exception.getMessage());
  }

  @Test
  void create_should_throw_when_longitude_is_above_180() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> ProximityReference.create(40.4168, 180.1));

    assertEquals(
        "proximity reference longitude must be between -180 and 180", exception.getMessage());
  }
}
