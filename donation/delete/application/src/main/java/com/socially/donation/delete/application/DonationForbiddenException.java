package com.socially.donation.delete.application;

public final class DonationForbiddenException extends RuntimeException {

  public DonationForbiddenException(String donationId) {
    super("Donation forbidden: " + donationId);
  }
}
