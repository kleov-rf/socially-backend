package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MediaStorageS3PropertiesValidatorTest {

  private static final String BUCKET = "socially-media";
  private static final String REGION = "us-east-1";
  private static final String CDN_BASE_URL = "https://cdn.example.com";
  private static final Duration PRESIGN_DURATION = Duration.ofMinutes(15);

  private static MediaStorageProperties propertiesWithS3(String bucket, String region) {
    return new MediaStorageProperties(
        new MediaStorageProperties.S3(bucket, region, null, null, null),
        new MediaStorageProperties.Cdn(CDN_BASE_URL),
        new MediaStorageProperties.Presign(PRESIGN_DURATION));
  }

  @Test
  void validateS3Properties_should_throw_when_s3_is_null() {
    MediaStorageProperties properties =
        new MediaStorageProperties(
            null,
            new MediaStorageProperties.Cdn(CDN_BASE_URL),
            new MediaStorageProperties.Presign(PRESIGN_DURATION));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> MediaStorageS3PropertiesValidator.validateS3Properties(properties));

    assertEquals("media.storage.s3 must be configured", exception.getMessage());
  }

  @Test
  void validateS3Properties_should_throw_when_bucket_not_configured() {
    MediaStorageProperties properties = propertiesWithS3("", REGION);

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> MediaStorageS3PropertiesValidator.validateS3Properties(properties));

    assertEquals("media.storage.s3.bucket must be configured", exception.getMessage());
  }

  @Test
  void validateS3Properties_should_throw_when_region_not_configured() {
    MediaStorageProperties properties = propertiesWithS3(BUCKET, "");

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> MediaStorageS3PropertiesValidator.validateS3Properties(properties));

    assertEquals("media.storage.s3.region must be configured", exception.getMessage());
  }

  @Test
  void validateS3Properties_should_not_throw_when_s3_is_configured() {
    MediaStorageProperties properties = propertiesWithS3(BUCKET, REGION);

    assertDoesNotThrow(() -> MediaStorageS3PropertiesValidator.validateS3Properties(properties));
  }
}
