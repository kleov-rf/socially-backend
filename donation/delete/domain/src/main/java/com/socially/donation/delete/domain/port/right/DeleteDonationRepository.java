package com.socially.donation.delete.domain.port.right;

import com.socially.commons.kernel.domain.valueobject.Id;

public interface DeleteDonationRepository {
  void deleteById(Id id);
}
