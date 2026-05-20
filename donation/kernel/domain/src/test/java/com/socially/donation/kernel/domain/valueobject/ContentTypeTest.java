package com.socially.donation.kernel.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

  @Test
  void from_should_accept_image_jpeg() {
    ContentType contentType = ContentType.from("image/jpeg");

    assertEquals("image/jpeg", contentType.value());
  }

  @Test
  void from_should_normalize_to_lowercase() {
    ContentType contentType = ContentType.from("IMAGE/PNG");

    assertEquals("image/png", contentType.value());
  }

  @Test
  void from_should_throw_when_value_is_null() {
    assertThrows(IllegalArgumentException.class, () -> ContentType.from(null));
  }

  @Test
  void from_should_throw_when_value_is_blank() {
    assertThrows(IllegalArgumentException.class, () -> ContentType.from("   "));
  }

  @Test
  void from_should_throw_when_content_type_not_allowed() {
    assertThrows(InvalidDonationImageException.class, () -> ContentType.from("image/gif"));
  }
}
