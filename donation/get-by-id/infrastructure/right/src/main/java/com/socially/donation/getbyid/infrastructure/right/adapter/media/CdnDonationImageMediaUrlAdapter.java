package com.socially.donation.getbyid.infrastructure.right.adapter.media;

import com.socially.donation.getbyid.domain.port.right.DonationImageMediaUrlPort;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import com.socially.donation.kernel.infrastructure.right.media.MediaUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class CdnDonationImageMediaUrlAdapter implements DonationImageMediaUrlPort {

  private final MediaStorageProperties mediaStorageProperties;

  @Override
  public String mediaUrlFor(StorageObjectKey storageObjectKey) {
    MediaUrlBuilder.validateCdnBaseUrl(mediaStorageProperties.cdn().baseUrl());
    return MediaUrlBuilder.buildMediaUrl(
        mediaStorageProperties.cdn().baseUrl(), storageObjectKey.value());
  }
}
