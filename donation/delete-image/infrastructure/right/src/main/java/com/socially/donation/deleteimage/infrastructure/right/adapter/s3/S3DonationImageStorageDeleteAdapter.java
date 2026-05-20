package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import com.socially.donation.deleteimage.domain.port.right.DonationImageStorageDeletePort;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import org.springframework.stereotype.Component;

@Component
public final class S3DonationImageStorageDeleteAdapter implements DonationImageStorageDeletePort {

  @Override
  public void deleteObject(StorageObjectKey storageObjectKey) {
    throw new UnsupportedOperationException("Donation image storage delete not implemented yet");
  }
}
