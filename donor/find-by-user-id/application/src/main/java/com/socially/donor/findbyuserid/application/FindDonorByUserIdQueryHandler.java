package com.socially.donor.findbyuserid.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.findbyuserid.domain.port.right.FindDonorByUserIdRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonorByUserIdQueryHandler implements FindDonorByUserIdUseCase {

  private final FindDonorByUserIdRepository findDonorByUserIdRepository;

  @Override
  public Optional<Donor> execute(FindDonorByUserIdQuery query) {
    Id userId = Id.from(query.userId());
    return findDonorByUserIdRepository.findByUserId(userId);
  }
}
