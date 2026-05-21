package com.socially.donation.createimage.infrastructure.right.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationImageS3ConfigurationTest {

  private static final String BUCKET = "socially-media";
  private static final String REGION = "us-east-1";
  private static final String MINISTACK_ENDPOINT = "http://ministack:4566";
  private static final String LOCALHOST_ENDPOINT = "http://localhost:4566";
  private static final Duration PRESIGN_DURATION = Duration.ofMinutes(15);

  private static MediaStorageProperties properties(MediaStorageProperties.S3 s3) {
    return new MediaStorageProperties(
        s3,
        new MediaStorageProperties.Cdn("https://cdn.example.com"),
        new MediaStorageProperties.Presign(PRESIGN_DURATION));
  }

  private static MediaStorageProperties.S3 s3(String bucket, String region) {
    return new MediaStorageProperties.S3(bucket, region, null, null, null, null);
  }

  private static MediaStorageProperties.S3 s3WithEndpoints(
      String endpointUrl, String publicEndpointUrl) {
    return new MediaStorageProperties.S3(
        BUCKET, REGION, endpointUrl, publicEndpointUrl, null, null);
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

  @Test
  void resolvePresignerEndpointUrl_should_prefer_public_endpoint_when_both_are_set() {
    MediaStorageProperties.S3 s3 = s3WithEndpoints(MINISTACK_ENDPOINT, LOCALHOST_ENDPOINT);

    assertEquals(LOCALHOST_ENDPOINT, DonationImageS3Configuration.resolvePresignerEndpointUrl(s3));
  }

  @Test
  void resolvePresignerEndpointUrl_should_fall_back_to_server_endpoint_when_public_is_null() {
    MediaStorageProperties.S3 s3 = s3WithEndpoints(LOCALHOST_ENDPOINT, null);

    assertEquals(LOCALHOST_ENDPOINT, DonationImageS3Configuration.resolvePresignerEndpointUrl(s3));
  }

  @Test
  void resolvePresignerEndpointUrl_should_fall_back_to_server_endpoint_when_public_is_blank() {
    MediaStorageProperties.S3 s3 = s3WithEndpoints(MINISTACK_ENDPOINT, "   ");

    assertEquals(MINISTACK_ENDPOINT, DonationImageS3Configuration.resolvePresignerEndpointUrl(s3));
  }
}
