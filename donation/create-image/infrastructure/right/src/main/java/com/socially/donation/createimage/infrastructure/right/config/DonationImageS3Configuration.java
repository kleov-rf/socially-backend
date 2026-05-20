package com.socially.donation.createimage.infrastructure.right.config;

import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import java.net.URI;
import java.util.Objects;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@EnableConfigurationProperties(MediaStorageProperties.class)
public class DonationImageS3Configuration {

  @Bean
  public S3Client s3Client(MediaStorageProperties mediaStorageProperties) {
    validateS3Properties(mediaStorageProperties);

    var builder =
        S3Client.builder()
            .region(Region.of(mediaStorageProperties.s3().region()))
            .credentialsProvider(credentialsProvider(mediaStorageProperties));

    if (StringUtils.hasText(mediaStorageProperties.s3().endpointUrl())) {
      builder
          .endpointOverride(URI.create(mediaStorageProperties.s3().endpointUrl()))
          .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
    }

    return builder.build();
  }

  @Bean
  public S3Presigner s3Presigner(S3Client s3Client, MediaStorageProperties mediaStorageProperties) {
    validateS3Properties(mediaStorageProperties);

    var builder =
        S3Presigner.builder()
            .region(Region.of(mediaStorageProperties.s3().region()))
            .credentialsProvider(credentialsProvider(mediaStorageProperties));

    if (StringUtils.hasText(mediaStorageProperties.s3().endpointUrl())) {
      builder
          .endpointOverride(URI.create(mediaStorageProperties.s3().endpointUrl()))
          .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
    }

    return builder.build();
  }

  private static AwsCredentialsProvider credentialsProvider(
      MediaStorageProperties mediaStorageProperties) {
    if (!StringUtils.hasText(mediaStorageProperties.s3().endpointUrl())) {
      return DefaultCredentialsProvider.create();
    }

    String accessKeyId =
        StringUtils.hasText(mediaStorageProperties.s3().accessKeyId())
            ? mediaStorageProperties.s3().accessKeyId()
            : "test";
    String secretAccessKey =
        StringUtils.hasText(mediaStorageProperties.s3().secretAccessKey())
            ? mediaStorageProperties.s3().secretAccessKey()
            : "test";

    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKeyId, secretAccessKey));
  }

  public static void validateS3Properties(MediaStorageProperties mediaStorageProperties) {
    if (Objects.isNull(mediaStorageProperties.s3())) {
      throw new IllegalStateException("media.storage.s3 must be configured");
    }

    if (!StringUtils.hasText(mediaStorageProperties.s3().bucket())) {
      throw new IllegalStateException("media.storage.s3.bucket must be configured");
    }

    if (!StringUtils.hasText(mediaStorageProperties.s3().region())) {
      throw new IllegalStateException("media.storage.s3.region must be configured");
    }
  }
}
