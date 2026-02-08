package com.socially.donation.application.create;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.application.create.mapper.CreateDonationMapper;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonationCommandHandler {

  private final DonationRepository donationRepository;

  public void handle(CreateDonationCommand command) {
    Donation donation = CreateDonationMapper.toDonation(command);
    donationRepository.save(donation);
  }
}
