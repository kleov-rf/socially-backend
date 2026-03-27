package com.socially.donation.update.application;

public final class DonationNotFoundException extends RuntimeException {

  public DonationNotFoundException(String donationId) {
    super("Donation not found: " + donationId);
  }
}
