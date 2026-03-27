package com.socially.donation.delete.domain.port.right;

import com.socially.donation.kernel.domain.valueobject.Id;

public interface DeleteDonationRepository {
  void deleteById(Id id);
}
