package com.socially.donation.createimage.application.output.mapper;

import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.kernel.domain.entity.DonationImage;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationImageResultMapper {

  public CreateDonationImageResult toResult(
      DonationImage image, PresignedDonationImageUpload presignedUpload) {
    return new CreateDonationImageResult(
        image.id().value().toString(),
        presignedUpload.uploadUrl(),
        presignedUpload.mediaUrl(),
        image.contentType().value(),
        image.sizeBytes(),
        image.primary());
  }
}
