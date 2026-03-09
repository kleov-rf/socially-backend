package com.socially.donation.application.create;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.application.create.mapper.CreateDonationCommandMapper;
import com.socially.donation.application.port.left.CreateDonationUseCase;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.right.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonationCommandHandler implements CreateDonationUseCase {

  private final DonationRepository donationRepository;
  private final CreateDonationCommandMapper createDonationCommandMapper;

  @Override
  public void execute(CreateDonationCommand command) {
    Donation donation = createDonationCommandMapper.toDomain(command);
    donationRepository.save(donation);
  }
}
