package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationImageEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationImageEntityMapperTest {

  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String STORAGE_OBJECT_KEY = "donations/abc/images/key.jpg";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long SIZE_BYTES = 1024L;
  private static final Boolean PRIMARY_FLAG = Boolean.TRUE;
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  private static final DonationImage DONATION_IMAGE =
      DonationImage.create(
          Id.from(IMAGE_ID),
          StorageObjectKey.from(STORAGE_OBJECT_KEY),
          ContentType.from(CONTENT_TYPE),
          SIZE_BYTES,
          PRIMARY_FLAG,
          CREATED_AT);

  private static final DonationImageEntity IMAGE_ENTITY =
      DonationImageEntity.create(
          UUID.fromString(IMAGE_ID),
          STORAGE_OBJECT_KEY,
          CONTENT_TYPE,
          SIZE_BYTES,
          PRIMARY_FLAG,
          CREATED_AT);

  @InjectMocks private DonationImageEntityMapper donationImageEntityMapper;

  @Test
  void toEntity_should_map_id() {
    DonationImageEntity result = donationImageEntityMapper.toEntity(DONATION_IMAGE);

    assertEquals(UUID.fromString(IMAGE_ID), result.getId());
  }

  @Test
  void toEntity_should_map_storage_object_key() {
    DonationImageEntity result = donationImageEntityMapper.toEntity(DONATION_IMAGE);

    assertEquals(STORAGE_OBJECT_KEY, result.getStorageObjectKey());
  }

  @Test
  void toEntity_should_map_content_type() {
    DonationImageEntity result = donationImageEntityMapper.toEntity(DONATION_IMAGE);

    assertEquals(CONTENT_TYPE, result.getContentType());
  }

  @Test
  void toEntity_should_map_size_bytes() {
    DonationImageEntity result = donationImageEntityMapper.toEntity(DONATION_IMAGE);

    assertEquals(SIZE_BYTES, result.getSizeBytes());
  }

  @Test
  void toEntity_should_map_primary_flag() {
    DonationImageEntity result = donationImageEntityMapper.toEntity(DONATION_IMAGE);

    assertEquals(PRIMARY_FLAG, result.getPrimaryFlag());
  }

  @Test
  void toEntity_should_map_created_at() {
    DonationImageEntity result = donationImageEntityMapper.toEntity(DONATION_IMAGE);

    assertEquals(CREATED_AT, result.getCreatedAt());
  }

  @Test
  void toDomain_should_map_id() {
    DonationImage result = donationImageEntityMapper.toDomain(IMAGE_ENTITY);

    assertEquals(Id.from(IMAGE_ID), result.id());
  }

  @Test
  void toDomain_should_map_storage_object_key() {
    DonationImage result = donationImageEntityMapper.toDomain(IMAGE_ENTITY);

    assertEquals(StorageObjectKey.from(STORAGE_OBJECT_KEY), result.storageObjectKey());
  }

  @Test
  void toDomain_should_map_content_type() {
    DonationImage result = donationImageEntityMapper.toDomain(IMAGE_ENTITY);

    assertEquals(ContentType.from(CONTENT_TYPE), result.contentType());
  }

  @Test
  void toDomain_should_map_size_bytes() {
    DonationImage result = donationImageEntityMapper.toDomain(IMAGE_ENTITY);

    assertEquals(SIZE_BYTES, result.sizeBytes());
  }

  @Test
  void toDomain_should_map_primary_flag() {
    DonationImage result = donationImageEntityMapper.toDomain(IMAGE_ENTITY);

    assertEquals(PRIMARY_FLAG, result.primary());
  }

  @Test
  void toDomain_should_map_created_at() {
    DonationImage result = donationImageEntityMapper.toDomain(IMAGE_ENTITY);

    assertEquals(CREATED_AT, result.createdAt());
  }
}
