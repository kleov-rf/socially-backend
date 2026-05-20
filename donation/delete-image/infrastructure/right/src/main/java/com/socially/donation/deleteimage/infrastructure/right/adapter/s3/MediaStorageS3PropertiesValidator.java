package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import java.util.Objects;
import org.springframework.util.StringUtils;

final class MediaStorageS3PropertiesValidator {

  private MediaStorageS3PropertiesValidator() {}

  static void validateS3Properties(MediaStorageProperties mediaStorageProperties) {
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
