package com.socially.donation.delete.application;

import com.socially.auth.kernel.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class DeleteDonationCommandHandler implements DeleteDonationUseCase {

  private final FindDonationByIdRepository findDonationByIdRepository;
  private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  private final DeleteDonationRepository donationRepository;

  @Override
  public void execute(DeleteDonationCommand command) {
    Id donationId = Id.from(command.id());
    Donation donation =
        findDonationByIdRepository
            .findById(donationId)
            .orElseThrow(() -> new DonationNotFoundException(command.id()));

    User user = getAuthenticatedUserUseCase.execute(command.principal());
    Optional<Donor> donor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));

    if (donor.isEmpty()) {
      throw new DonationForbiddenException(command.id());
    }

    if (!donation.belongsToDonor(donor.get().id())) {
      throw new DonationForbiddenException(command.id());
    }

    donationRepository.deleteById(donationId);
  }
}
