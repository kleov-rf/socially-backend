package com.socially.donation.kernel.domain.exception;

import com.socially.commons.kernel.domain.valueobject.Id;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class DonationImageNotFoundException extends RuntimeException {

  public DonationImageNotFoundException(Id donationId, Id imageId) {
    super("Donation image not found: " + imageId.value() + " on donation " + donationId.value());
  }
}
