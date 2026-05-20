package com.socially.donation.createimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;
import com.socially.donation.createimage.infrastructure.right.config.MediaStorageProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3DonationImageUploadAdapterTest {

  private static final String BUCKET = "socially-media";
  private static final String CDN_BASE_URL = "https://cdn.example.com";
  private static final Duration PRESIGN_DURATION = Duration.ofMinutes(15);
  private static final String STORAGE_OBJECT_KEY = "donations/abc/images/key.jpg";
  private static final String UPLOAD_URL = "https://s3.example.com/presigned-put";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long SIZE_BYTES = 1024L;

  @Mock private S3PresignedPutUrlGenerator presignedPutUrlGenerator;
  @Mock private MediaStorageProperties mediaStorageProperties;

  @InjectMocks private S3DonationImageUploadAdapter adapter;

  private static PresignedDonationImageUploadRequest request() {
    return new PresignedDonationImageUploadRequest(STORAGE_OBJECT_KEY, CONTENT_TYPE, SIZE_BYTES);
  }

  private void stubMediaStorageProperties(String cdnBaseUrl) {
    when(mediaStorageProperties.s3())
        .thenReturn(new MediaStorageProperties.S3(BUCKET, "us-east-1", null, null, null));
    when(mediaStorageProperties.cdn()).thenReturn(new MediaStorageProperties.Cdn(cdnBaseUrl));
    when(mediaStorageProperties.presign())
        .thenReturn(new MediaStorageProperties.Presign(PRESIGN_DURATION));
  }

  @Test
  void issueUpload_should_call_generator_with_bucket_key_content_type_and_size_bytes() {
    stubMediaStorageProperties(CDN_BASE_URL);
    when(presignedPutUrlGenerator.generatePutUrl(
            eq(BUCKET),
            eq(STORAGE_OBJECT_KEY),
            eq(CONTENT_TYPE),
            eq(SIZE_BYTES),
            eq(PRESIGN_DURATION)))
        .thenReturn(UPLOAD_URL);

    adapter.issueUpload(request());

    verify(presignedPutUrlGenerator)
        .generatePutUrl(BUCKET, STORAGE_OBJECT_KEY, CONTENT_TYPE, SIZE_BYTES, PRESIGN_DURATION);
  }

  @Test
  void issueUpload_should_return_upload_url_from_generator() {
    stubMediaStorageProperties(CDN_BASE_URL);
    when(presignedPutUrlGenerator.generatePutUrl(
            eq(BUCKET),
            eq(STORAGE_OBJECT_KEY),
            eq(CONTENT_TYPE),
            eq(SIZE_BYTES),
            eq(PRESIGN_DURATION)))
        .thenReturn(UPLOAD_URL);

    PresignedDonationImageUpload result = adapter.issueUpload(request());

    assertEquals(UPLOAD_URL, result.uploadUrl());
  }

  @Test
  void issueUpload_should_return_media_url_as_cdn_base_plus_key() {
    stubMediaStorageProperties(CDN_BASE_URL);
    when(presignedPutUrlGenerator.generatePutUrl(
            eq(BUCKET),
            eq(STORAGE_OBJECT_KEY),
            eq(CONTENT_TYPE),
            eq(SIZE_BYTES),
            eq(PRESIGN_DURATION)))
        .thenReturn(UPLOAD_URL);

    PresignedDonationImageUpload result = adapter.issueUpload(request());

    assertEquals(CDN_BASE_URL + "/" + STORAGE_OBJECT_KEY, result.mediaUrl());
  }

  @Test
  void issueUpload_should_throw_when_cdn_base_url_is_blank() {
    stubMediaStorageProperties("");

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> adapter.issueUpload(request()));

    assertEquals("media.storage.cdn.base-url must be configured", exception.getMessage());
  }

  @Test
  void issueUpload_should_normalize_cdn_base_url_trailing_slash() {
    stubMediaStorageProperties(CDN_BASE_URL + "/");
    when(presignedPutUrlGenerator.generatePutUrl(
            eq(BUCKET),
            eq(STORAGE_OBJECT_KEY),
            eq(CONTENT_TYPE),
            eq(SIZE_BYTES),
            eq(PRESIGN_DURATION)))
        .thenReturn(UPLOAD_URL);

    PresignedDonationImageUpload result = adapter.issueUpload(request());

    assertEquals(CDN_BASE_URL + "/" + STORAGE_OBJECT_KEY, result.mediaUrl());
  }
}
