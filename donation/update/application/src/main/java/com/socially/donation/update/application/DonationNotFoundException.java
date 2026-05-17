package com.socially.donation.update.application;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class DonationNotFoundException extends RuntimeException {

  public DonationNotFoundException(String donationId) {
    super("Donation not found: " + donationId);
  }
}
