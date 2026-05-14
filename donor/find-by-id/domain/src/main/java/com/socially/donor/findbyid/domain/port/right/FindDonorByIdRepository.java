package com.socially.donor.findbyid.domain.port.right;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;

public interface FindDonorByIdRepository {
  Optional<Donor> findById(Id donorId);
}
