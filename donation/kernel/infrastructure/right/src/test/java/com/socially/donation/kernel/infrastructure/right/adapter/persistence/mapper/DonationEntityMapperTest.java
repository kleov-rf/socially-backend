package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationImageEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationEntityMapperTest {

  @Mock private DonationImageEntityMapper donationImageEntityMapper;

  @InjectMocks private DonationEntityMapper donationEntityMapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440002";
  private static final String STORAGE_OBJECT_KEY = "donations/abc/images/key.jpg";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long SIZE_BYTES = 1024L;
  private static final Boolean PRIMARY_FLAG = Boolean.TRUE;
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:30:00Z");
  private static final Instant IMAGE_CREATED_AT = Instant.parse("2024-06-15T10:00:00Z");
  private static final DonationLocation LOCATION =
      DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038);
  private static final Donation DONATION =
      Donation.create(
          Id.from(DONATION_ID),
          Id.from(DONOR_ID),
          Title.from("Test Title"),
          Description.from("Test Description"),
          LOCATION,
          CREATED_AT,
          LAST_UPDATED_AT);

  private static final Instant ENTITY_CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
  private static final Instant ENTITY_LAST_UPDATED_AT = Instant.parse("2025-02-01T18:00:00Z");

  private static final DonationImage DONATION_IMAGE =
      DonationImage.create(
          Id.from(IMAGE_ID),
          StorageObjectKey.from(STORAGE_OBJECT_KEY),
          ContentType.from(CONTENT_TYPE),
          SIZE_BYTES,
          PRIMARY_FLAG,
          IMAGE_CREATED_AT);

  private static final DonationImageEntity IMAGE_ENTITY =
      DonationImageEntity.create(
          UUID.fromString(IMAGE_ID),
          STORAGE_OBJECT_KEY,
          CONTENT_TYPE,
          SIZE_BYTES,
          PRIMARY_FLAG,
          IMAGE_CREATED_AT);

  private static Donation donationWithImage() {
    return Donation.create(
        Id.from(DONATION_ID),
        Id.from(DONOR_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        LOCATION,
        CREATED_AT,
        LAST_UPDATED_AT,
        List.of(DONATION_IMAGE));
  }

  private static DonationEntity entityWithLocation() {
    return DonationEntity.create(
        UUID.fromString(DONATION_ID),
        UUID.fromString(DONOR_ID),
        "Entity Title",
        "Entity Description",
        ENTITY_CREATED_AT,
        ENTITY_LAST_UPDATED_AT,
        "Calle Mayor 1, Madrid",
        40.4168,
        -3.7038);
  }

  private static DonationEntity entityWithOneImage() {
    DonationEntity entity = entityWithLocation();
    DonationImageEntity imageEntity =
        DonationImageEntity.create(
            UUID.fromString(IMAGE_ID),
            STORAGE_OBJECT_KEY,
            CONTENT_TYPE,
            SIZE_BYTES,
            PRIMARY_FLAG,
            IMAGE_CREATED_AT);
    imageEntity.attachTo(entity);
    entity.getImages().add(imageEntity);
    return entity;
  }

  private DonationEntity donationEntityWithNullImages() {
    DonationEntity entity = mock(DonationEntity.class);
    when(entity.getId()).thenReturn(UUID.fromString(DONATION_ID));
    when(entity.getDonorId()).thenReturn(UUID.fromString(DONOR_ID));
    when(entity.getTitle()).thenReturn("Entity Title");
    when(entity.getDescription()).thenReturn("Entity Description");
    when(entity.getCreatedAt()).thenReturn(ENTITY_CREATED_AT);
    when(entity.getLastUpdatedAt()).thenReturn(ENTITY_LAST_UPDATED_AT);
    when(entity.getLocationAddress()).thenReturn("Calle Mayor 1, Madrid");
    when(entity.getLocationLatitude()).thenReturn(40.4168);
    when(entity.getLocationLongitude()).thenReturn(-3.7038);
    when(entity.getImages()).thenReturn(null);
    return entity;
  }

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
  void toEntity_should_map_location_address() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals("Calle Mayor 1, Madrid", result.getLocationAddress());
  }

  @Test
  void toEntity_should_map_location_latitude() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals(40.4168, result.getLocationLatitude());
  }

  @Test
  void toEntity_should_map_location_longitude() {
    DonationEntity result = donationEntityMapper.toEntity(DONATION);

    assertEquals(-3.7038, result.getLocationLongitude());
  }

  @Test
  void toEntity_should_not_call_image_mapper_when_donation_has_no_images() {
    donationEntityMapper.toEntity(DONATION);

    verifyNoInteractions(donationImageEntityMapper);
  }

  @Test
  void toEntity_should_call_image_mapper_for_each_donation_image() {
    when(donationImageEntityMapper.toEntity(DONATION_IMAGE)).thenReturn(IMAGE_ENTITY);

    donationEntityMapper.toEntity(donationWithImage());

    verify(donationImageEntityMapper).toEntity(DONATION_IMAGE);
  }

  @Test
  void toEntity_should_add_mapped_image_to_donation_entity() {
    when(donationImageEntityMapper.toEntity(DONATION_IMAGE)).thenReturn(IMAGE_ENTITY);

    DonationEntity result = donationEntityMapper.toEntity(donationWithImage());

    assertEquals(1, result.getImages().size());
    assertEquals(IMAGE_ENTITY, result.getImages().getFirst());
  }

  @Test
  void toEntity_should_attach_mapped_image_to_donation_entity() {
    when(donationImageEntityMapper.toEntity(DONATION_IMAGE)).thenReturn(IMAGE_ENTITY);

    DonationEntity result = donationEntityMapper.toEntity(donationWithImage());

    assertEquals(result, result.getImages().getFirst().getDonation());
  }

  @Test
  void toDomain_should_map_id() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(Id.from(DONATION_ID), result.id());
  }

  @Test
  void toDomain_should_map_title() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(Title.from("Entity Title"), result.title());
  }

  @Test
  void toDomain_should_map_donor_id() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(Id.from(DONOR_ID), result.donorId());
  }

  @Test
  void toDomain_should_map_description() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(Description.from("Entity Description"), result.description());
  }

  @Test
  void toDomain_should_map_created_at() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(ENTITY_CREATED_AT, result.createdAt());
  }

  @Test
  void toDomain_should_map_last_updated_at() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(ENTITY_LAST_UPDATED_AT, result.lastUpdatedAt());
  }

  @Test
  void toDomain_should_map_location_when_all_columns_are_present() {
    Donation result = donationEntityMapper.toDomain(entityWithLocation());

    assertEquals(LOCATION, result.location());
  }

  @Test
  void toDomain_should_not_call_image_mapper_when_images_collection_is_null() {
    DonationEntity entity = donationEntityWithNullImages();

    donationEntityMapper.toDomain(entity);

    verifyNoInteractions(donationImageEntityMapper);
  }

  @Test
  void toDomain_should_map_empty_images_when_images_collection_is_null() {
    Donation result = donationEntityMapper.toDomain(donationEntityWithNullImages());

    assertTrue(result.images().isEmpty());
  }

  @Test
  void toDomain_should_call_image_mapper_for_each_entity_image_when_collection_initialized() {
    DonationEntity entity = entityWithOneImage();
    DonationImageEntity imageEntity = entity.getImages().getFirst();
    when(donationImageEntityMapper.toDomain(imageEntity)).thenReturn(DONATION_IMAGE);

    donationEntityMapper.toDomain(entity);

    verify(donationImageEntityMapper).toDomain(imageEntity);
  }

  @Test
  void toDomain_should_include_mapped_images_when_collection_initialized() {
    DonationEntity entity = entityWithOneImage();
    DonationImageEntity imageEntity = entity.getImages().getFirst();
    when(donationImageEntityMapper.toDomain(imageEntity)).thenReturn(DONATION_IMAGE);

    Donation result = donationEntityMapper.toDomain(entity);

    assertEquals(List.of(DONATION_IMAGE), result.images());
  }
}
