package com.socially.donation.update.application;

import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.update.application.input.UpdateDonationCommand;
import com.socially.donation.update.application.port.left.UpdateDonationUseCase;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class UpdateDonationCommandHandler implements UpdateDonationUseCase {

  private final FindDonationByIdRepository findDonationByIdRepository;
  private final UpdateDonationRepository updateDonationRepository;

  @Override
  public void execute(UpdateDonationCommand command) {
    Id donationId = Id.from(command.id());
    Donation donation =
        findDonationByIdRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.id()));

    if (Objects.nonNull(command.title())) {
      donation = donation.withTitle(Title.from(command.title()));
    }

    if (Objects.nonNull(command.description())) {
      donation = donation.withDescription(Description.from(command.description()));
    }

    updateDonationRepository.update(donation);
  }
}
