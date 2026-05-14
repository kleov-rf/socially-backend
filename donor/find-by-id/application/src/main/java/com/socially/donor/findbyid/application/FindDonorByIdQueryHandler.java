package com.socially.donor.findbyid.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyid.application.input.FindDonorByIdQuery;
import com.socially.donor.findbyid.application.port.left.FindDonorByIdUseCase;
import com.socially.donor.findbyid.domain.port.right.FindDonorByIdRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonorByIdQueryHandler implements FindDonorByIdUseCase {

  private final FindDonorByIdRepository findDonorByIdRepository;

  @Override
  public Optional<Donor> execute(FindDonorByIdQuery query) {
    Id donorId = Id.from(query.donorId());
    return findDonorByIdRepository.findById(donorId);
  }
}
