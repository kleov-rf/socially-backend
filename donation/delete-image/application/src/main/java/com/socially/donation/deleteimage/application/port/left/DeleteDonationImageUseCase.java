package com.socially.donation.deleteimage.application.port.left;

import com.socially.donation.deleteimage.application.input.DeleteDonationImageCommand;

public interface DeleteDonationImageUseCase {
  void execute(DeleteDonationImageCommand command);
}
