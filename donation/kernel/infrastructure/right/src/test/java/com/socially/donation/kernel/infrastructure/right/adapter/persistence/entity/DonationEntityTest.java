package com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationEntityTest {

  private static final UUID ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
  private static final UUID DONOR_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
  private static final String TITLE = "Test Title";
  private static final String DESCRIPTION = "Test Description";
  private static final String LOCATION_ADDRESS = "Calle Mayor 1, Madrid";
  private static final double LOCATION_LATITUDE = 40.4168;
  private static final double LOCATION_LONGITUDE = -3.7038;
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:30:00Z");

  private static DonationEntity createEntity() {
    return DonationEntity.create(
        ID,
        DONOR_ID,
        TITLE,
        DESCRIPTION,
        CREATED_AT,
        LAST_UPDATED_AT,
        LOCATION_ADDRESS,
        LOCATION_LATITUDE,
        LOCATION_LONGITUDE);
  }

  @Test
  void create_should_set_id() {
    DonationEntity result = createEntity();

    assertEquals(ID, result.getId());
  }

  @Test
  void create_should_set_title() {
    DonationEntity result = createEntity();

    assertEquals(TITLE, result.getTitle());
  }

  @Test
  void create_should_set_description() {
    DonationEntity result = createEntity();

    assertEquals(DESCRIPTION, result.getDescription());
  }

  @Test
  void create_should_set_created_at() {
    DonationEntity result = createEntity();

    assertEquals(CREATED_AT, result.getCreatedAt());
  }

  @Test
  void create_should_set_last_updated_at() {
    DonationEntity result = createEntity();

    assertEquals(LAST_UPDATED_AT, result.getLastUpdatedAt());
  }

  @Test
  void create_should_set_deleted_at_as_null() {
    DonationEntity result = createEntity();

    assertNull(result.getDeletedAt());
  }

  @Test
  void create_should_set_donor_id() {
    DonationEntity result = createEntity();

    assertEquals(DONOR_ID, result.getDonorId());
  }

  @Test
  void create_should_set_location_address() {
    DonationEntity result = createEntity();

    assertEquals(LOCATION_ADDRESS, result.getLocationAddress());
  }

  @Test
  void create_should_set_location_latitude() {
    DonationEntity result = createEntity();

    assertEquals(LOCATION_LATITUDE, result.getLocationLatitude());
  }

  @Test
  void create_should_set_location_longitude() {
    DonationEntity result = createEntity();

    assertEquals(LOCATION_LONGITUDE, result.getLocationLongitude());
  }
}
