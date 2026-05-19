package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.CreateDonationImageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationImageResponseMapperTest {

  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String UPLOAD_URL = "https://s3.example.com/upload";
  private static final String MEDIA_URL = "https://cdn.example.com/media/key";

  @InjectMocks private CreateDonationImageResponseMapper mapper;

  private static CreateDonationImageResult result() {
    return new CreateDonationImageResult(
        IMAGE_ID, UPLOAD_URL, MEDIA_URL, "image/jpeg", 1024L, true);
  }

  @Test
  void toResponse_should_map_image_id() {
    CreateDonationImageResponse response = mapper.toResponse(result());

    assertEquals(IMAGE_ID, response.imageId());
  }

  @Test
  void toResponse_should_map_upload_url() {
    CreateDonationImageResponse response = mapper.toResponse(result());

    assertEquals(UPLOAD_URL, response.uploadUrl());
  }

  @Test
  void toResponse_should_map_media_url() {
    CreateDonationImageResponse response = mapper.toResponse(result());

    assertEquals(MEDIA_URL, response.mediaUrl());
  }

  @Test
  void toResponse_should_map_content_type() {
    CreateDonationImageResponse response = mapper.toResponse(result());

    assertEquals("image/jpeg", response.contentType());
  }

  @Test
  void toResponse_should_map_size_bytes() {
    CreateDonationImageResponse response = mapper.toResponse(result());

    assertEquals(1024L, response.sizeBytes());
  }

  @Test
  void toResponse_should_map_primary() {
    CreateDonationImageResponse response = mapper.toResponse(result());

    assertTrue(response.primary());
  }
}
