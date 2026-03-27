package com.socially.donation.create.application;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.create.application.port.left.CreateDonationUseCase;
import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonationCommandHandler implements CreateDonationUseCase {

  private final CreateDonationRepository donationRepository;
  private final CreateDonationCommandMapper createDonationCommandMapper;

  @Override
  public void execute(CreateDonationCommand command) {
    Donation donation = createDonationCommandMapper.toDomain(command);
    donationRepository.create(donation);
  }
}
