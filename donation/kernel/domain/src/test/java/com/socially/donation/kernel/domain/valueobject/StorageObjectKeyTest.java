package com.socially.donation.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.commons.kernel.domain.valueobject.Id;
import org.junit.jupiter.api.Test;

class StorageObjectKeyTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";

  @Test
  void from_should_trim_value() {
    StorageObjectKey key = StorageObjectKey.from("  donations/abc/images/key.jpg  ");

    assertEquals("donations/abc/images/key.jpg", key.value());
  }

  @Test
  void from_should_throw_when_value_is_null() {
    assertThrows(IllegalArgumentException.class, () -> StorageObjectKey.from(null));
  }

  @Test
  void from_should_throw_when_value_is_blank() {
    assertThrows(IllegalArgumentException.class, () -> StorageObjectKey.from("   "));
  }

  @Test
  void forDonationImage_should_build_path_with_extension() {
    StorageObjectKey key =
        StorageObjectKey.forDonationImage(Id.from(DONATION_ID), Id.from(IMAGE_ID), "photo.jpg");

    assertEquals("donations/" + DONATION_ID + "/images/" + IMAGE_ID + ".jpg", key.value());
  }

  @Test
  void forDonationImage_should_omit_extension_when_file_name_has_none() {
    StorageObjectKey key =
        StorageObjectKey.forDonationImage(Id.from(DONATION_ID), Id.from(IMAGE_ID), "photo");

    assertEquals("donations/" + DONATION_ID + "/images/" + IMAGE_ID, key.value());
  }

  @Test
  void forDonationImage_should_throw_when_donation_id_is_null() {
    assertThrows(
        IllegalArgumentException.class,
        () -> StorageObjectKey.forDonationImage(null, Id.from(IMAGE_ID), "photo.jpg"));
  }

  @Test
  void forDonationImage_should_throw_when_image_id_is_null() {
    assertThrows(
        IllegalArgumentException.class,
        () -> StorageObjectKey.forDonationImage(Id.from(DONATION_ID), null, "photo.jpg"));
  }

  @Test
  void forDonationImage_should_throw_when_original_file_name_is_null() {
    assertThrows(
        IllegalArgumentException.class,
        () -> StorageObjectKey.forDonationImage(Id.from(DONATION_ID), Id.from(IMAGE_ID), null));
  }
}
