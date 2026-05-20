package com.socially.donation.deleteimage.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.deleteimage.application.input.DeleteDonationImageCommand;
import com.socially.donation.deleteimage.application.port.left.DeleteDonationImageUseCase;
import com.socially.donation.deleteimage.domain.port.right.DonationImageStorageDeletePort;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.exception.DonationImageNotFoundException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class DeleteDonationImageCommandHandler implements DeleteDonationImageUseCase {

  private final FindDonationByIdRepository findDonationByIdRepository;
  private final AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  private final DonationImageStorageDeletePort donationImageStorageDeletePort;
  private final UpdateDonationRepository updateDonationRepository;

  @Override
  public void execute(DeleteDonationImageCommand command) {
    Id donationId = Id.from(command.donationId());
    Donation donation =
        findDonationByIdRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.donationId()));

    assertDonationOwnedByPrincipalUseCase.execute(donation, command.principal());

    Id imageId = Id.from(command.imageId());
    DonationImage imageToRemove =
        donation.images().stream()
            .filter(image -> image.id().equals(imageId))
            .findFirst()
            .orElseThrow(() -> new DonationImageNotFoundException(donation.id(), imageId));

    donationImageStorageDeletePort.deleteObject(imageToRemove.storageObjectKey());

    Donation updatedDonation = donation.withImageRemoved(imageId);
    updateDonationRepository.update(updatedDonation);
  }
}
