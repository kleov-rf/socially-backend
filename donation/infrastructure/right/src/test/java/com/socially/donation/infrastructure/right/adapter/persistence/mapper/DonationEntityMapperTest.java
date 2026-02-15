package com.socially.donation.infrastructure.right.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import com.socially.donation.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DonationEntityMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Donation DONATION =
      Donation.create(
          Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));

  private static final DonationEntity ENTITY =
      new DonationEntity(UUID.fromString(DONATION_ID), "Entity Title", "Entity Description");

  @Test
  void toEntity_should_map_id() {
    DonationEntity result = DonationEntityMapper.toEntity(DONATION);

    assertEquals(UUID.fromString(DONATION_ID), result.getId());
  }

  @Test
  void toEntity_should_map_title() {
    DonationEntity result = DonationEntityMapper.toEntity(DONATION);

    assertEquals("Test Title", result.getTitle());
  }

  @Test
  void toEntity_should_map_description() {
    DonationEntity result = DonationEntityMapper.toEntity(DONATION);

    assertEquals("Test Description", result.getDescription());
  }

  @Test
  void toDomain_should_map_id() {
    Donation result = DonationEntityMapper.toDomain(ENTITY);

    assertEquals(Id.from(DONATION_ID), result.id());
  }

  @Test
  void toDomain_should_map_title() {
    Donation result = DonationEntityMapper.toDomain(ENTITY);

    assertEquals(Title.from("Entity Title"), result.title());
  }

  @Test
  void toDomain_should_map_description() {
    Donation result = DonationEntityMapper.toDomain(ENTITY);

    assertEquals(Description.from("Entity Description"), result.description());
  }
}
