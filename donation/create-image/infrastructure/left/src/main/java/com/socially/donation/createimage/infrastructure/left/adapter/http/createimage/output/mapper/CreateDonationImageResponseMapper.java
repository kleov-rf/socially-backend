package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.mapper;

import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.CreateDonationImageResponse;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationImageResponseMapper {

  public CreateDonationImageResponse toResponse(CreateDonationImageResult result) {
    return new CreateDonationImageResponse(
        result.imageId(),
        result.uploadUrl(),
        result.mediaUrl(),
        result.contentType(),
        result.sizeBytes(),
        result.primary());
  }
}
