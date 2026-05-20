package com.socially.donation.createimage.infrastructure.right.adapter.s3;

import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@RequiredArgsConstructor
public class S3PresignedPutUrlGenerator {

  private final S3Presigner s3Presigner;

  public String generatePutUrl(
      String bucket,
      String key,
      String contentType,
      Long contentLength,
      Duration signatureDuration) {
    if (Objects.isNull(bucket)) {
      throw new IllegalArgumentException("bucket cannot be null");
    }

    if (Objects.isNull(key)) {
      throw new IllegalArgumentException("key cannot be null");
    }

    if (Objects.isNull(contentType)) {
      throw new IllegalArgumentException("content type cannot be null");
    }

    if (Objects.isNull(contentLength)) {
      throw new IllegalArgumentException("content length cannot be null");
    }

    if (Objects.isNull(signatureDuration)) {
      throw new IllegalArgumentException("signature duration cannot be null");
    }

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .contentLength(contentLength)
            .build();

    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(signatureDuration)
            .putObjectRequest(putObjectRequest)
            .build();

    return s3Presigner.presignPutObject(presignRequest).url().toExternalForm();
  }
}
