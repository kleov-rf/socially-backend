package com.socially.donation.createimage.infrastructure.right.adapter.s3;

import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;
import com.socially.donation.createimage.domain.port.right.DonationImageUploadPort;
import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import com.socially.donation.kernel.infrastructure.right.media.MediaUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class S3DonationImageUploadAdapter implements DonationImageUploadPort {

  private final S3PresignedPutUrlGenerator presignedPutUrlGenerator;
  private final MediaStorageProperties mediaStorageProperties;

  @Override
  public PresignedDonationImageUpload issueUpload(PresignedDonationImageUploadRequest request) {
    String uploadUrl =
        presignedPutUrlGenerator.generatePutUrl(
            mediaStorageProperties.s3().bucket(),
            request.storageObjectKey(),
            request.contentType(),
            request.sizeBytes(),
            mediaStorageProperties.presign().duration());

    MediaUrlBuilder.validateCdnBaseUrl(mediaStorageProperties.cdn().baseUrl());
    String mediaUrl =
        MediaUrlBuilder.buildMediaUrl(
            mediaStorageProperties.cdn().baseUrl(), request.storageObjectKey());

    return new PresignedDonationImageUpload(uploadUrl, mediaUrl);
  }
}
