package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class S3ObjectDeleterTest {

  private static final String BUCKET = "socially-media";
  private static final String KEY = "donations/abc/images/key.jpg";

  @Mock private S3Client s3Client;

  @InjectMocks private S3ObjectDeleter deleter;

  private static S3Exception s3ExceptionWithErrorCode(String errorCode) {
    return (S3Exception)
        S3Exception.builder()
            .message("S3 error")
            .awsErrorDetails(AwsErrorDetails.builder().errorCode(errorCode).build())
            .build();
  }

  @Test
  void deleteObject_should_call_s3_client_with_delete_request() {
    deleter.deleteObject(BUCKET, KEY);

    ArgumentCaptor<DeleteObjectRequest> requestCaptor =
        ArgumentCaptor.forClass(DeleteObjectRequest.class);
    verify(s3Client).deleteObject(requestCaptor.capture());
    assertEquals(BUCKET, requestCaptor.getValue().bucket());
    assertEquals(KEY, requestCaptor.getValue().key());
  }

  @Test
  void deleteObject_should_not_throw_when_no_such_key() {
    when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
        .thenThrow(NoSuchKeyException.builder().message("not found").build());

    assertDoesNotThrow(() -> deleter.deleteObject(BUCKET, KEY));
  }

  @Test
  void deleteObject_should_not_throw_when_s3_exception_has_no_such_key_error_code() {
    when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
        .thenThrow(s3ExceptionWithErrorCode("NoSuchKey"));

    assertDoesNotThrow(() -> deleter.deleteObject(BUCKET, KEY));
  }

  @Test
  void deleteObject_should_propagate_when_s3_fails_with_other_error() {
    when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
        .thenThrow(s3ExceptionWithErrorCode("AccessDenied"));

    assertThrows(S3Exception.class, () -> deleter.deleteObject(BUCKET, KEY));
  }
}
