package com.socially.donation.createimage.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.application.input.mapper.CreateDonationImageCommandMapper;
import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.application.output.mapper.CreateDonationImageResultMapper;
import com.socially.donation.createimage.application.output.mapper.PresignedDonationImageUploadRequestMapper;
import com.socially.donation.createimage.application.port.left.CreateDonationImageUseCase;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.createimage.domain.port.right.DonationImageUploadPort;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateDonationImageCommandHandler implements CreateDonationImageUseCase {

  private final FindDonationByIdRepository findDonationByIdRepository;
  private final AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  private final UpdateDonationRepository updateDonationRepository;
  private final CreateDonationImageCommandMapper createDonationImageCommandMapper;
  private final PresignedDonationImageUploadRequestMapper presignedDonationImageUploadRequestMapper;
  private final CreateDonationImageResultMapper createDonationImageResultMapper;
  private final DonationImageUploadPort donationImageUploadPort;
  private final Clock clock;

  @Override
  public CreateDonationImageResult execute(CreateDonationImageCommand command) {
    Id donationId = Id.from(command.donationId());
    Donation donation =
        findDonationByIdRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.donationId()));

    assertDonationOwnedByPrincipalUseCase.execute(donation, command.principal());

    DonationImage image =
        createDonationImageCommandMapper.toDonationImage(command, clock.instant());
    Donation updatedDonation = donation.withImageAdded(image);
    updateDonationRepository.update(updatedDonation);

    PresignedDonationImageUpload presignedUpload =
        donationImageUploadPort.issueUpload(
            presignedDonationImageUploadRequestMapper.toRequest(image));

    return createDonationImageResultMapper.toResult(image, presignedUpload);
  }
}
