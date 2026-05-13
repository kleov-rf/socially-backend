package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationEntityMapperTest {

  @InjectMocks private DonationEntityMapper donationEntityMapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:30:00Z");
  private static final Donation DONATION =
      Donation.create(
          Id.from(DONATION_ID),
          DonorId.from(DONOR_ID),
          Title.from("Test Title"),
          Description.from("Test Description"),
          CREATED_AT,
          LAST_UPDATED_AT);

  private static final Instant ENTITY_CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
  private static final Instant ENTITY_LAST_UPDATED_AT = Instant.parse("2025-02-01T18:00:00Z");
  private static final DonationEntity ENTITY =
      DonationEntity.create(
          UUID.fromString(DONATION_ID),
          UUID.fromString(DONOR_ID),
          "Entity Title",
          "Entity Description",
          ENTITY_CREATED_AT,
          ENTITY_LAST_UPDATED_AT);

  @Test
  void toEntity_should_map_id() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals(UUID.fromString(DONATION_ID), result.getId());
  }

  @Test
  void toEntity_should_map_title() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals("Test Title", result.getTitle());
  }

  @Test
  void toEntity_should_map_donor_id() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals(UUID.fromString(DONOR_ID), result.getDonorId());
  }

  @Test
  void toEntity_should_map_description() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals("Test Description", result.getDescription());
  }

  @Test
  void toEntity_should_map_created_at() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals(CREATED_AT, result.getCreatedAt());
  }

  @Test
  void toEntity_should_map_last_updated_at() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals(LAST_UPDATED_AT, result.getLastUpdatedAt());
  }

  @Test
  void toEntity_should_map_deleted_at_as_null() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertNull(result.getDeletedAt());
  }

  @Test
  void toDomain_should_map_id() {
    Donation result = donationEntityMapper.toDomain(ENTITY);

    assertEquals(Id.from(DONATION_ID), result.id());
  }

  @Test
  void toDomain_should_map_title() {
    Donation result = donationEntityMapper.toDomain(ENTITY);

    assertEquals(Title.from("Entity Title"), result.title());
  }

  @Test
  void toDomain_should_map_donor_id() {
    Donation result = donationEntityMapper.toDomain(ENTITY);

    assertEquals(DonorId.from(DONOR_ID), result.donorId());
  }

  @Test
  void toDomain_should_map_description() {
    Donation result = donationEntityMapper.toDomain(ENTITY);

    assertEquals(Description.from("Entity Description"), result.description());
  }

  @Test
  void toDomain_should_map_created_at() {
    Donation result = donationEntityMapper.toDomain(ENTITY);

    assertEquals(ENTITY_CREATED_AT, result.createdAt());
  }

  @Test
  void toDomain_should_map_last_updated_at() {
    Donation result = donationEntityMapper.toDomain(ENTITY);

    assertEquals(ENTITY_LAST_UPDATED_AT, result.lastUpdatedAt());
  }
}
