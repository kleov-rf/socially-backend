package com.socially.donation.kernel.application.port.left;

import com.socially.donation.kernel.domain.entity.Donation;
import java.security.Principal;

public interface AssertDonationOwnedByPrincipalUseCase {

  void execute(Donation donation, Principal principal);
}
