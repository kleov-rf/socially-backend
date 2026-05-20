package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
public final class S3ObjectDeleter {

  private final S3Client s3Client;

  public void deleteObject(String bucket, String key) {
    try {
      s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    } catch (NoSuchKeyException ignored) {
      // Idempotent delete: object already absent.
    } catch (S3Exception exception) {
      if (isNoSuchKey(exception)) {
        return;
      }
      throw exception;
    }
  }

  private static boolean isNoSuchKey(S3Exception exception) {
    return exception.awsErrorDetails() != null
        && "NoSuchKey".equals(exception.awsErrorDetails().errorCode());
  }
}
