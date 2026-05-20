package com.socially.donation.createimage.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.kernel.domain.entity.DonationImage;
import java.security.Principal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationImageCommandMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  private final CreateDonationImageCommandMapper mapper = new CreateDonationImageCommandMapper();

  private static CreateDonationImageCommand command() {
    return new CreateDonationImageCommand(
        DONATION_ID, "photo.jpg", "image/jpeg", 1024L, Boolean.TRUE, PRINCIPAL);
  }

  @Test
  void toDonationImage_should_map_content_type() {
    DonationImage image = mapper.toDonationImage(command(), CREATED_AT);

    assertEquals("image/jpeg", image.contentType().value());
  }

  @Test
  void toDonationImage_should_map_size_bytes() {
    DonationImage image = mapper.toDonationImage(command(), CREATED_AT);

    assertEquals(1024L, image.sizeBytes());
  }

  @Test
  void toDonationImage_should_map_primary() {
    DonationImage image = mapper.toDonationImage(command(), CREATED_AT);

    assertEquals(Boolean.TRUE, image.primary());
  }

  @Test
  void toDonationImage_should_map_created_at() {
    DonationImage image = mapper.toDonationImage(command(), CREATED_AT);

    assertEquals(CREATED_AT, image.createdAt());
  }

  @Test
  void toDonationImage_should_generate_storage_object_key_with_donation_id() {
    DonationImage image = mapper.toDonationImage(command(), CREATED_AT);

    assertTrue(
        image.storageObjectKey().value().startsWith("donations/" + DONATION_ID + "/images/"));
    assertTrue(image.storageObjectKey().value().endsWith(".jpg"));
  }
}
