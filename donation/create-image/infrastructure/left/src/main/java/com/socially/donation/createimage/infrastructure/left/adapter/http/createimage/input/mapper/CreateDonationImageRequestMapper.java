package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.mapper;

import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.CreateDonationImageRequest;
import java.security.Principal;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationImageRequestMapper {

  public CreateDonationImageCommand toCommand(
      String donationId, CreateDonationImageRequest request, Principal principal) {
    return new CreateDonationImageCommand(
        donationId,
        request.originalFileName(),
        request.contentType(),
        request.sizeBytes(),
        request.primary(),
        principal);
  }
}
