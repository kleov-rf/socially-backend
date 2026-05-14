package com.socially.donor.findbyuserid.application.port.left;

import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;

public interface FindDonorByUserIdUseCase {
  Optional<Donor> execute(FindDonorByUserIdQuery query);
}
