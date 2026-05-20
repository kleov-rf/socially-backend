package com.socially.donation.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class InvalidDonationImageException extends RuntimeException {

  public InvalidDonationImageException(String message) {
    super(message);
  }
}
