package com.socially.donation.create.application;

import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.create.application.port.left.CreateDonationUseCase;
import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.port.left.CreateDonorUseCase;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonationCommandHandler implements CreateDonationUseCase {

  private final GetCurrentAuthUserUseCase getCurrentAuthUserUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  private final CreateDonorUseCase createDonorUseCase;
  private final CreateDonationRepository donationRepository;
  private final CreateDonationCommandMapper createDonationCommandMapper;
  private final Clock clock;

  @Override
  public void execute(CreateDonationCommand command) {
    User user = getCurrentAuthUserUseCase.execute(command.principal());
    Optional<Donor> existingDonor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));

    String donorId;
    if (existingDonor.isEmpty()) {
      donorId = UUID.randomUUID().toString();
      createDonorUseCase.execute(
          new CreateDonorCommand(
              donorId,
              user.id().value().toString(),
              user.email().value(),
              user.givenName(),
              user.familyName()));
    } else {
      donorId = existingDonor.get().id().value().toString();
    }

    Donation donation = createDonationCommandMapper.toDomain(command, donorId, clock.instant());
    donationRepository.create(donation);
  }
}
