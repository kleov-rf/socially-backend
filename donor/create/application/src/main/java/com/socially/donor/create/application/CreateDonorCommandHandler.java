package com.socially.donor.create.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.input.mapper.CreateDonorCommandMapper;
import com.socially.donor.create.application.port.left.CreateDonorUseCase;
import com.socially.donor.create.domain.port.right.CreateDonorRepository;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonorCommandHandler implements CreateDonorUseCase {

  private final CreateDonorRepository donorRepository;
  private final CreateDonorCommandMapper createDonorCommandMapper;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  private final Clock clock;

  @Override
  public void execute(CreateDonorCommand command) {
    FindDonorByUserIdQuery query = new FindDonorByUserIdQuery(command.userId());
    Optional<Donor> existingDonor = findDonorByUserIdUseCase.execute(query);

    if (existingDonor.isPresent()) {
      return;
    }

    Id donorId = Id.generate();
    Donor donor = createDonorCommandMapper.toDomain(donorId, command, clock.instant());
    donorRepository.create(donor);
  }
}
