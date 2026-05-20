package com.socially.donation.createimage.application.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationImageResultMapperTest {

  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @InjectMocks private CreateDonationImageResultMapper mapper;

  private static DonationImage image() {
    return DonationImage.create(
        Id.from(IMAGE_ID),
        StorageObjectKey.from("donations/abc/images/key.jpg"),
        ContentType.from("image/png"),
        1024L,
        Boolean.FALSE,
        CREATED_AT);
  }

  private static PresignedDonationImageUpload presignedUpload() {
    return new PresignedDonationImageUpload(
        "https://s3.example.com/upload", "https://cdn.example.com/media/key");
  }

  @Test
  void toResult_should_map_image_id() {
    CreateDonationImageResult result = mapper.toResult(image(), presignedUpload());

    assertEquals(IMAGE_ID, result.imageId());
  }

  @Test
  void toResult_should_map_upload_url() {
    CreateDonationImageResult result = mapper.toResult(image(), presignedUpload());

    assertEquals("https://s3.example.com/upload", result.uploadUrl());
  }

  @Test
  void toResult_should_map_media_url() {
    CreateDonationImageResult result = mapper.toResult(image(), presignedUpload());

    assertEquals("https://cdn.example.com/media/key", result.mediaUrl());
  }

  @Test
  void toResult_should_map_content_type() {
    CreateDonationImageResult result = mapper.toResult(image(), presignedUpload());

    assertEquals("image/png", result.contentType());
  }

  @Test
  void toResult_should_map_size_bytes() {
    CreateDonationImageResult result = mapper.toResult(image(), presignedUpload());

    assertEquals(1024L, result.sizeBytes());
  }

  @Test
  void toResult_should_map_primary() {
    CreateDonationImageResult result = mapper.toResult(image(), presignedUpload());

    assertEquals(Boolean.FALSE, result.primary());
  }
}
