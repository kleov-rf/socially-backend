package com.socially.donation.createimage.application;

import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.application.port.left.CreateDonationImageUseCase;
import org.springframework.stereotype.Service;

@Service
public final class CreateDonationImageCommandHandler implements CreateDonationImageUseCase {

  @Override
  public CreateDonationImageResult execute(CreateDonationImageCommand command) {
    throw new UnsupportedOperationException("CreateDonationImage handler not implemented yet");
  }
}
