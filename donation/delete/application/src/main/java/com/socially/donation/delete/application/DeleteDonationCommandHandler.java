package com.socially.donation.delete.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class DeleteDonationCommandHandler implements DeleteDonationUseCase {

  private final FindDonationByIdRepository findDonationByIdRepository;
  private final AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  private final DeleteDonationRepository donationRepository;

  @Override
  public void execute(DeleteDonationCommand command) {
    Id donationId = Id.from(command.id());
    Donation donation =
        findDonationByIdRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.id()));

    assertDonationOwnedByPrincipalUseCase.execute(donation, command.principal());

    donationRepository.deleteById(donationId);
  }
}
