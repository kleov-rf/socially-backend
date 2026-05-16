package com.socially.donor.create.application;

import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.input.mapper.CreateDonorCommandMapper;
import com.socially.donor.create.application.port.left.CreateDonorUseCase;
import com.socially.donor.create.domain.port.right.CreateDonorRepository;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Clock;
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
    if (findDonorByUserIdUseCase
        .execute(new FindDonorByUserIdQuery(command.userId()))
        .isPresent()) {
      return;
    }
    Donor donor = createDonorCommandMapper.toDomain(command, clock.instant());
    donorRepository.create(donor);
  }
}
