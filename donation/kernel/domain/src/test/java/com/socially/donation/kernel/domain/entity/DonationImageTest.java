package com.socially.donation.kernel.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DonationImageTest {

  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  private static DonationImage createImage() {
    return DonationImage.create(
        Id.from(IMAGE_ID),
        StorageObjectKey.from("donations/abc/images/key.jpg"),
        ContentType.from("image/jpeg"),
        1024L,
        Boolean.TRUE,
        CREATED_AT);
  }

  @Test
  void create_should_set_id() {
    DonationImage image = createImage();

    assertEquals(IMAGE_ID, image.id().value().toString());
  }

  @Test
  void create_should_set_storage_object_key() {
    DonationImage image = createImage();

    assertEquals("donations/abc/images/key.jpg", image.storageObjectKey().value());
  }

  @Test
  void create_should_set_content_type() {
    DonationImage image = createImage();

    assertEquals("image/jpeg", image.contentType().value());
  }

  @Test
  void create_should_set_size_bytes() {
    DonationImage image = createImage();

    assertEquals(1024L, image.sizeBytes());
  }

  @Test
  void create_should_set_primary() {
    DonationImage image = createImage();

    assertTrue(image.primary());
  }

  @Test
  void create_should_set_created_at() {
    DonationImage image = createImage();

    assertEquals(CREATED_AT, image.createdAt());
  }

  @Test
  void create_should_throw_when_size_bytes_is_zero() {
    assertThrows(
        InvalidDonationImageException.class,
        () ->
            DonationImage.create(
                Id.from(IMAGE_ID),
                StorageObjectKey.from("donations/abc/images/key.jpg"),
                ContentType.from("image/jpeg"),
                0L,
                Boolean.FALSE,
                CREATED_AT));
  }

  @Test
  void create_should_throw_when_size_bytes_exceeds_maximum() {
    assertThrows(
        InvalidDonationImageException.class,
        () ->
            DonationImage.create(
                Id.from(IMAGE_ID),
                StorageObjectKey.from("donations/abc/images/key.jpg"),
                ContentType.from("image/jpeg"),
                DonationImage.MAX_SIZE_BYTES + 1,
                Boolean.FALSE,
                CREATED_AT));
  }

  @Test
  void withPrimary_should_update_primary_flag() {
    DonationImage image = createImage().withPrimary(Boolean.FALSE);

    assertFalse(image.primary());
  }
}
