package com.socially.donation.delete.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class DeleteDonationCommandHandler implements DeleteDonationUseCase {

  private final DeleteDonationRepository donationRepository;

  @Override
  public void execute(DeleteDonationCommand command) {
    donationRepository.deleteById(Id.from(command.id()));
  }
}
