package com.socially.donor.findbyuserid.domain.port.right;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;

public interface FindDonorByUserIdRepository {
  Optional<Donor> findByUserId(Id userId);
}
