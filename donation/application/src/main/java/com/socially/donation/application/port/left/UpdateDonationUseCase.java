package com.socially.donation.application.port.left;

import com.socially.donation.application.update.input.UpdateDonationCommand;

public interface UpdateDonationUseCase {
  void execute(UpdateDonationCommand command);
}
