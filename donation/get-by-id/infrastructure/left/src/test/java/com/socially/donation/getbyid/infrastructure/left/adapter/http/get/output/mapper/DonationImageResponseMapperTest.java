package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationImageResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationImageResponseMapperTest {

  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String MEDIA_URL = "https://cdn.example.com/donations/abc/images/key.jpg";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long SIZE_BYTES = 1024L;
  private static final Boolean PRIMARY = Boolean.TRUE;

  @InjectMocks private DonationImageResponseMapper mapper;

  private static DonationImageDto imageDto() {
    return new DonationImageDto(IMAGE_ID, MEDIA_URL, CONTENT_TYPE, SIZE_BYTES, PRIMARY);
  }

  @Test
  void toResponse_should_map_image_id() {
    DonationImageResponseDto response = mapper.toResponse(imageDto());

    assertEquals(IMAGE_ID, response.imageId());
  }

  @Test
  void toResponse_should_map_media_url() {
    DonationImageResponseDto response = mapper.toResponse(imageDto());

    assertEquals(MEDIA_URL, response.mediaUrl());
  }

  @Test
  void toResponse_should_map_content_type() {
    DonationImageResponseDto response = mapper.toResponse(imageDto());

    assertEquals(CONTENT_TYPE, response.contentType());
  }

  @Test
  void toResponse_should_map_size_bytes() {
    DonationImageResponseDto response = mapper.toResponse(imageDto());

    assertEquals(SIZE_BYTES, response.sizeBytes());
  }

  @Test
  void toResponse_should_map_primary() {
    DonationImageResponseDto response = mapper.toResponse(imageDto());

    assertEquals(PRIMARY, response.primary());
  }
}
