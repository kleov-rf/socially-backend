package com.socially.donation.update.domain.port.right;

import com.socially.donation.kernel.domain.entity.Donation;

public interface UpdateDonationRepository {
  void update(Donation donation);
}
