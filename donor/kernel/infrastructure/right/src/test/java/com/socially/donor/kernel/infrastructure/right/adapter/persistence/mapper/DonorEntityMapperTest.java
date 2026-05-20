package com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity.DonorEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonorEntityMapperTest {

  @InjectMocks private DonorEntityMapper donorEntityMapper;

  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  private static final Donor DONOR =
      Donor.create(
          Id.from(DONOR_ID), Id.from(USER_ID), "donor@example.com", "Jane", "Doe", CREATED_AT);

  private static final Instant ENTITY_CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
  private static final DonorEntity ENTITY =
      DonorEntity.create(
          UUID.fromString(DONOR_ID),
          UUID.fromString(USER_ID),
          "entity@example.com",
          "EntityGiven",
          "EntityFamily",
          ENTITY_CREATED_AT);

  @Test
  void toEntity_should_map_id() {
    DonorEntity result = donorEntityMapper.toEntity(DONOR);

    assertEquals(UUID.fromString(DONOR_ID), result.getId());
  }

  @Test
  void toEntity_should_map_user_id() {
    DonorEntity result = donorEntityMapper.toEntity(DONOR);

    assertEquals(UUID.fromString(USER_ID), result.getUserId());
  }

  @Test
  void toEntity_should_map_email() {
    DonorEntity result = donorEntityMapper.toEntity(DONOR);

    assertEquals("donor@example.com", result.getEmail());
  }

  @Test
  void toEntity_should_map_given_name() {
    DonorEntity result = donorEntityMapper.toEntity(DONOR);

    assertEquals("Jane", result.getGivenName());
  }

  @Test
  void toEntity_should_map_family_name() {
    DonorEntity result = donorEntityMapper.toEntity(DONOR);

    assertEquals("Doe", result.getFamilyName());
  }

  @Test
  void toEntity_should_map_created_at() {
    DonorEntity result = donorEntityMapper.toEntity(DONOR);

    assertEquals(CREATED_AT, result.getCreatedAt());
  }

  @Test
  void toDomain_should_map_id() {
    Donor result = donorEntityMapper.toDomain(ENTITY);

    assertEquals(Id.from(DONOR_ID), result.id());
  }

  @Test
  void toDomain_should_map_user_id() {
    Donor result = donorEntityMapper.toDomain(ENTITY);

    assertEquals(Id.from(USER_ID), result.userId());
  }

  @Test
  void toDomain_should_map_email() {
    Donor result = donorEntityMapper.toDomain(ENTITY);

    assertEquals("entity@example.com", result.email());
  }

  @Test
  void toDomain_should_map_given_name() {
    Donor result = donorEntityMapper.toDomain(ENTITY);

    assertEquals("EntityGiven", result.givenName());
  }

  @Test
  void toDomain_should_map_family_name() {
    Donor result = donorEntityMapper.toDomain(ENTITY);

    assertEquals("EntityFamily", result.familyName());
  }

  @Test
  void toDomain_should_map_created_at() {
    Donor result = donorEntityMapper.toDomain(ENTITY);

    assertEquals(ENTITY_CREATED_AT, result.createdAt());
  }
}
