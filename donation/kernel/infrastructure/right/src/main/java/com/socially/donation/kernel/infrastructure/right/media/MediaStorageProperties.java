package com.socially.donation.kernel.infrastructure.right.media;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "media.storage")
public record MediaStorageProperties(S3 s3, Cdn cdn, Presign presign) {

  public record S3(
      String bucket,
      String region,
      String endpointUrl,
      String accessKeyId,
      String secretAccessKey) {}

  public record Cdn(String baseUrl) {}

  public record Presign(Duration duration) {}
}
