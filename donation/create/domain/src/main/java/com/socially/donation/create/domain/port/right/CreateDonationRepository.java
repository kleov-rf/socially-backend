package com.socially.donation.create.domain.port.right;

import com.socially.donation.kernel.domain.entity.Donation;

public interface CreateDonationRepository {
  void create(Donation donation);
}
