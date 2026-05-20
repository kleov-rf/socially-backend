package com.socially.donation.getbyid.domain.port.right;

import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;

public interface DonationImageMediaUrlPort {

  String mediaUrlFor(StorageObjectKey storageObjectKey);
}
