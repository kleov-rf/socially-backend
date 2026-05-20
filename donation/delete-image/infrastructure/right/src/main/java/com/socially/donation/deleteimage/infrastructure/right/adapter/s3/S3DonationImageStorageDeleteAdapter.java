package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import com.socially.donation.deleteimage.domain.port.right.DonationImageStorageDeletePort;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class S3DonationImageStorageDeleteAdapter implements DonationImageStorageDeletePort {

  private final S3ObjectDeleter s3ObjectDeleter;
  private final MediaStorageProperties mediaStorageProperties;

  @Override
  public void deleteObject(StorageObjectKey storageObjectKey) {
    MediaStorageS3PropertiesValidator.validateS3Properties(mediaStorageProperties);
    s3ObjectDeleter.deleteObject(mediaStorageProperties.s3().bucket(), storageObjectKey.value());
  }
}
