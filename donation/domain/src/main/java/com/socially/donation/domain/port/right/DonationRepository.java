package com.socially.donation.domain.port.right;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Id;
import java.util.Optional;

public interface DonationRepository {
  void save(Donation donation);

  Optional<Donation> findById(Id id);
}
