package com.socially.donor.findbyid.application.port.left;

import com.socially.donor.findbyid.application.input.FindDonorByIdQuery;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;

public interface FindDonorByIdUseCase {
  Optional<Donor> execute(FindDonorByIdQuery query);
}
