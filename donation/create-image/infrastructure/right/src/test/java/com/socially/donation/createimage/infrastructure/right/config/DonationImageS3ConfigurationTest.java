package com.socially.donation.createimage.infrastructure.right.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DonationImageS3ConfigurationTest {

  private static final String BUCKET = "socially-media";
  private static final String REGION = "us-east-1";
  private static final Duration PRESIGN_DURATION = Duration.ofMinutes(15);

  private static MediaStorageProperties properties(MediaStorageProperties.S3 s3) {
    return new MediaStorageProperties(
        s3,
        new MediaStorageProperties.Cdn("https://cdn.example.com"),
        new MediaStorageProperties.Presign(PRESIGN_DURATION));
  }

  private static MediaStorageProperties.S3 s3(String bucket, String region) {
    return new MediaStorageProperties.S3(bucket, region, null, null, null);
  }

  @Test
  void validateS3Properties_should_throw_when_s3_is_null() {
    MediaStorageProperties mediaStorageProperties = properties(null);

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> DonationImageS3Configuration.validateS3Properties(mediaStorageProperties));

    assertEquals("media.storage.s3 must be configured", exception.getMessage());
  }

  @Test
  void validateS3Properties_should_throw_when_bucket_is_blank() {
    MediaStorageProperties mediaStorageProperties = properties(s3("", REGION));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> DonationImageS3Configuration.validateS3Properties(mediaStorageProperties));

    assertEquals("media.storage.s3.bucket must be configured", exception.getMessage());
  }

  @Test
  void validateS3Properties_should_throw_when_region_is_blank() {
    MediaStorageProperties mediaStorageProperties = properties(s3(BUCKET, "   "));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> DonationImageS3Configuration.validateS3Properties(mediaStorageProperties));

    assertEquals("media.storage.s3.region must be configured", exception.getMessage());
  }

  @Test
  void validateS3Properties_should_not_throw_when_s3_bucket_and_region_are_set() {
    MediaStorageProperties mediaStorageProperties = properties(s3(BUCKET, REGION));

    assertDoesNotThrow(
        () -> DonationImageS3Configuration.validateS3Properties(mediaStorageProperties));
  }
}
