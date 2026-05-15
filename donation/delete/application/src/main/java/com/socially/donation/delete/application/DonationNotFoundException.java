package com.socially.donation.delete.application;

public final class DonationNotFoundException extends RuntimeException {

  public DonationNotFoundException(String donationId) {
    super("Donation not found: " + donationId);
  }
}
