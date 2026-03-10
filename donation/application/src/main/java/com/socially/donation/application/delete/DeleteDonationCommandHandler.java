package com.socially.donation.application.delete;

import com.socially.donation.application.delete.input.DeleteDonationCommand;
import com.socially.donation.application.port.left.DeleteDonationUseCase;
import com.socially.donation.domain.port.right.DonationRepository;
import com.socially.donation.domain.valueobject.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class DeleteDonationCommandHandler implements DeleteDonationUseCase {

  private final DonationRepository donationRepository;

  @Override
  public void execute(DeleteDonationCommand command) {
    donationRepository.deleteById(Id.from(command.id()));
  }
}
