package com.socially.donation.deleteimage.application;

import com.socially.donation.deleteimage.application.input.DeleteDonationImageCommand;
import com.socially.donation.deleteimage.application.port.left.DeleteDonationImageUseCase;
import org.springframework.stereotype.Service;

@Service
public final class DeleteDonationImageCommandHandler implements DeleteDonationImageUseCase {

  @Override
  public void execute(DeleteDonationImageCommand command) {
    throw new UnsupportedOperationException("DeleteDonationImage handler not implemented yet");
  }
}
