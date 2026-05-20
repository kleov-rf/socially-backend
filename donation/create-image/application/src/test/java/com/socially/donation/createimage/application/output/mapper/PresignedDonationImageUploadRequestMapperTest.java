package com.socially.donation.createimage.application.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PresignedDonationImageUploadRequestMapperTest {

  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @InjectMocks private PresignedDonationImageUploadRequestMapper mapper;

  private static DonationImage image() {
    return DonationImage.create(
        Id.from("660e8400-e29b-41d4-a716-446655440001"),
        StorageObjectKey.from("donations/abc/images/key.jpg"),
        ContentType.from("image/jpeg"),
        2048L,
        Boolean.TRUE,
        CREATED_AT);
  }

  @Test
  void toRequest_should_map_storage_object_key() {
    PresignedDonationImageUploadRequest request = mapper.toRequest(image());

    assertEquals("donations/abc/images/key.jpg", request.storageObjectKey());
  }

  @Test
  void toRequest_should_map_content_type() {
    PresignedDonationImageUploadRequest request = mapper.toRequest(image());

    assertEquals("image/jpeg", request.contentType());
  }

  @Test
  void toRequest_should_map_size_bytes() {
    PresignedDonationImageUploadRequest request = mapper.toRequest(image());

    assertEquals(2048L, request.sizeBytes());
  }
}
