package com.socially.donation.application.port.left;

import com.socially.donation.application.delete.input.DeleteDonationCommand;

public interface DeleteDonationUseCase {
  void execute(DeleteDonationCommand command);
}
