package com.socially.donation.deleteimage.domain.port.right;

import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;

public interface DonationImageStorageDeletePort {

  void deleteObject(StorageObjectKey storageObjectKey);
}
