package com.socially.donation.createimage.application.output.mapper;

import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;
import com.socially.donation.kernel.domain.entity.DonationImage;
import org.springframework.stereotype.Component;

@Component
public class PresignedDonationImageUploadRequestMapper {

  public PresignedDonationImageUploadRequest toRequest(DonationImage image) {
    return new PresignedDonationImageUploadRequest(
        image.storageObjectKey().value(), image.contentType().value(), image.sizeBytes());
  }
}
