package com.socially.donation.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public final class DonationForbiddenException extends RuntimeException {

  public DonationForbiddenException(String donationId) {
    super("Donation forbidden: " + donationId);
  }
}
