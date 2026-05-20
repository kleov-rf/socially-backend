package com.socially.donation.getbyid.infrastructure.right.adapter.media;

import com.socially.donation.getbyid.domain.port.right.DonationImageMediaUrlPort;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import org.springframework.stereotype.Component;

@Component
public final class CdnDonationImageMediaUrlAdapter implements DonationImageMediaUrlPort {

  @Override
  public String mediaUrlFor(StorageObjectKey storageObjectKey) {
    throw new UnsupportedOperationException(
        "Donation image media URL resolution is not implemented.");
  }
}
