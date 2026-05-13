package com.socially.donor.create.domain.port.right;

import com.socially.donor.kernel.domain.entity.Donor;

public interface CreateDonorRepository {
  void create(Donor donor);
}
