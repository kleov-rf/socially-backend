package com.socially.donation.update.application.port.left;

import com.socially.donation.update.application.input.UpdateDonationCommand;

public interface UpdateDonationUseCase {
  void execute(UpdateDonationCommand command);
}
