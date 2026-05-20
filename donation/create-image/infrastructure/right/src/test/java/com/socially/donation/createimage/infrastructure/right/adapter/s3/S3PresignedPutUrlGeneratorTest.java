package com.socially.donation.createimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@ExtendWith(MockitoExtension.class)
class S3PresignedPutUrlGeneratorTest {

  private static final String BUCKET = "socially-media";
  private static final String KEY = "donations/abc/images/key.jpg";
  private static final String CONTENT_TYPE = "image/jpeg";
  private static final Long SIZE_BYTES = 1024L;
  private static final Duration DURATION = Duration.ofMinutes(15);
  private static final String PRESIGNED_URL = "https://s3.example.com/presigned";

  @Mock private S3Presigner s3Presigner;

  @InjectMocks private S3PresignedPutUrlGenerator generator;

  @Test
  void generatePutUrl_should_throw_when_bucket_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generatePutUrl(null, KEY, CONTENT_TYPE, SIZE_BYTES, DURATION));

    assertEquals("bucket cannot be null", exception.getMessage());
  }

  @Test
  void generatePutUrl_should_throw_when_key_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generatePutUrl(BUCKET, null, CONTENT_TYPE, SIZE_BYTES, DURATION));

    assertEquals("key cannot be null", exception.getMessage());
  }

  @Test
  void generatePutUrl_should_throw_when_content_type_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generatePutUrl(BUCKET, KEY, null, SIZE_BYTES, DURATION));

    assertEquals("content type cannot be null", exception.getMessage());
  }

  @Test
  void generatePutUrl_should_throw_when_content_length_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generatePutUrl(BUCKET, KEY, CONTENT_TYPE, null, DURATION));

    assertEquals("content length cannot be null", exception.getMessage());
  }

  @Test
  void generatePutUrl_should_throw_when_signature_duration_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generatePutUrl(BUCKET, KEY, CONTENT_TYPE, SIZE_BYTES, null));

    assertEquals("signature duration cannot be null", exception.getMessage());
  }

  @Test
  void generatePutUrl_should_return_presigned_url_from_presigner() throws MalformedURLException {
    PresignedPutObjectRequest presignedRequest =
        org.mockito.Mockito.mock(PresignedPutObjectRequest.class);
    when(presignedRequest.url()).thenReturn(new URL(PRESIGNED_URL));
    when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
        .thenReturn(presignedRequest);

    String actual = generator.generatePutUrl(BUCKET, KEY, CONTENT_TYPE, SIZE_BYTES, DURATION);

    assertEquals(PRESIGNED_URL, actual);
  }

  @Test
  void generatePutUrl_should_pass_bucket_key_content_type_length_and_duration_to_presigner()
      throws MalformedURLException {
    PresignedPutObjectRequest presignedRequest =
        org.mockito.Mockito.mock(PresignedPutObjectRequest.class);
    when(presignedRequest.url()).thenReturn(new URL(PRESIGNED_URL));
    when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
        .thenReturn(presignedRequest);

    generator.generatePutUrl(BUCKET, KEY, CONTENT_TYPE, SIZE_BYTES, DURATION);

    ArgumentCaptor<PutObjectPresignRequest> captor =
        ArgumentCaptor.forClass(PutObjectPresignRequest.class);
    verify(s3Presigner).presignPutObject(captor.capture());

    PutObjectPresignRequest presignRequest = captor.getValue();
    assertEquals(DURATION, presignRequest.signatureDuration());

    PutObjectRequest putObjectRequest = presignRequest.putObjectRequest();
    assertEquals(BUCKET, putObjectRequest.bucket());
    assertEquals(KEY, putObjectRequest.key());
    assertEquals(CONTENT_TYPE, putObjectRequest.contentType());
    assertEquals(SIZE_BYTES, putObjectRequest.contentLength());
  }
}
