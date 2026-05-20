package com.socially.donation.getbyid.application.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationImageDtoMapperTest {

  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String STORAGE_OBJECT_KEY =
      "donations/" + DONATION_ID + "/images/" + IMAGE_ID + ".jpg";
  private static final StorageObjectKey KEY = StorageObjectKey.from(STORAGE_OBJECT_KEY);
  private static final String MEDIA_URL = "https://cdn.example.com/" + STORAGE_OBJECT_KEY;
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long SIZE_BYTES = 1024L;
  private static final Boolean PRIMARY = Boolean.TRUE;
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @InjectMocks private DonationImageDtoMapper mapper;

  private static DonationImage sampleImage() {
    return DonationImage.create(
        Id.from(IMAGE_ID), KEY, ContentType.from(CONTENT_TYPE), SIZE_BYTES, PRIMARY, CREATED_AT);
  }

  @Test
  void toDto_should_map_image_id() {
    DonationImageDto result = mapper.toDto(sampleImage(), MEDIA_URL);

    assertEquals(IMAGE_ID, result.imageId());
  }

  @Test
  void toDto_should_map_media_url() {
    DonationImageDto result = mapper.toDto(sampleImage(), MEDIA_URL);

    assertEquals(MEDIA_URL, result.mediaUrl());
  }

  @Test
  void toDto_should_map_content_type() {
    DonationImageDto result = mapper.toDto(sampleImage(), MEDIA_URL);

    assertEquals(CONTENT_TYPE, result.contentType());
  }

  @Test
  void toDto_should_map_size_bytes() {
    DonationImageDto result = mapper.toDto(sampleImage(), MEDIA_URL);

    assertEquals(SIZE_BYTES, result.sizeBytes());
  }

  @Test
  void toDto_should_map_primary() {
    DonationImageDto result = mapper.toDto(sampleImage(), MEDIA_URL);

    assertEquals(PRIMARY, result.primary());
  }
}
