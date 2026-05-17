package com.socially.donation.kernel.application;

import com.socially.auth.kernel.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class AssertDonationOwnedByPrincipalCommandHandler
    implements AssertDonationOwnedByPrincipalUseCase {

  private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;

  @Override
  public void execute(Donation donation, Principal principal) {
    String donationId = donation.id().value().toString();
    User user = getAuthenticatedUserUseCase.execute(principal);
    Optional<Donor> donor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));

    if (donor.isEmpty()) {
      throw new DonationForbiddenException(donationId);
    }

    if (!donation.belongsToDonor(donor.get().id())) {
      throw new DonationForbiddenException(donationId);
    }
  }
}
