package com.socially.donation.getbyid.domain.port.right;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import java.util.Optional;

public interface FindDonationByIdRepository {
  Optional<Donation> findById(Id id);
}
