package com.socially.donation.application.update;

import com.socially.donation.application.port.left.UpdateDonationUseCase;
import com.socially.donation.application.update.input.UpdateDonationCommand;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.right.DonationRepository;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class UpdateDonationCommandHandler implements UpdateDonationUseCase {

  private final DonationRepository donationRepository;

  @Override
  public void execute(UpdateDonationCommand command) {
    Id donationId = Id.from(command.id());
    Donation existingDonation =
        donationRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.id()));

    Donation updatedDonation = buildUpdatedDonation(command, existingDonation);
    donationRepository.update(updatedDonation);
  }

  private Donation buildUpdatedDonation(UpdateDonationCommand command, Donation existingDonation) {
    Title updatedTitle =
        Objects.isNull(command.title()) ? existingDonation.title() : Title.from(command.title());
    Description updatedDescription =
        Objects.isNull(command.description())
            ? existingDonation.description()
            : Description.from(command.description());

    return Donation.create(existingDonation.id(), updatedTitle, updatedDescription);
  }
}
