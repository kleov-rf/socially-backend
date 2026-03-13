package com.socially.donation.application.update;

public final class DonationNotFoundException extends RuntimeException {

  public DonationNotFoundException(String donationId) {
    super("Donation not found: " + donationId);
  }
}
