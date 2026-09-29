package com.socially.donation.update.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.update.application.input.UpdateDonationCommand;
import com.socially.donation.update.application.port.left.UpdateDonationUseCase;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class UpdateDonationCommandHandler implements UpdateDonationUseCase {

  private final FindDonationByIdRepository findDonationByIdRepository;
  private final AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  private final UpdateDonationRepository updateDonationRepository;
  private final Clock clock;

  @Override
  public void execute(UpdateDonationCommand command) {
    Id donationId = Id.from(command.id());
    Donation donation =
        findDonationByIdRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.id()));

    assertDonationOwnedByPrincipalUseCase.execute(donation, command.principal());

    Donation updated = applyUpdates(donation, command);

    updateDonationRepository.update(updated);
  }

  private Donation applyUpdates(Donation donation, UpdateDonationCommand command) {
    if (hasNoUpdates(command)) {
      return donation;
    }

    Instant now = clock.instant();

    Donation withTitle =
        command.title().map(title -> donation.withTitle(Title.from(title), now)).orElse(donation);

    Donation withDescription =
        command
            .description()
            .map(description -> withTitle.withDescription(Description.from(description), now))
            .orElse(withTitle);

    return command
        .location()
        .map(
            location ->
                withDescription.withLocation(
                    DonationLocation.from(
                        location.address(), location.latitude(), location.longitude()),
                    now))
        .orElse(withDescription);
  }

  private boolean hasNoUpdates(UpdateDonationCommand command) {
    return command.title().isEmpty()
        && command.description().isEmpty()
        && command.location().isEmpty();
  }
}
