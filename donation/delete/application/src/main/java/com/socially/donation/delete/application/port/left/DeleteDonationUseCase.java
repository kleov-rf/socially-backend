package com.socially.donation.delete.application.port.left;

import com.socially.donation.delete.application.input.DeleteDonationCommand;

public interface DeleteDonationUseCase {
  void execute(DeleteDonationCommand command);
}
