package com.socially.donation.create.application;

import com.socially.auth.me.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.create.application.port.left.CreateDonationUseCase;
import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.domain.exception.DonorNotFoundAfterCreateException;
import com.socially.user.kernel.domain.entity.User;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonationCommandHandler implements CreateDonationUseCase {

  private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  private final CreateDonationRepository donationRepository;
  private final CreateDonationCommandMapper createDonationCommandMapper;
  private final Clock clock;

  @Override
  public void execute(CreateDonationCommand command) {
    User user = getAuthenticatedUserUseCase.execute(command.principal());

    Donor donor =
        findDonorByUserIdUseCase
            .execute(new FindDonorByUserIdQuery(user.id().value().toString()))
            .orElseThrow(DonorNotFoundAfterCreateException::new);

    Donation donation = createDonationCommandMapper.toDomain(command, donor, clock.instant());
    donationRepository.create(donation);
  }
}
