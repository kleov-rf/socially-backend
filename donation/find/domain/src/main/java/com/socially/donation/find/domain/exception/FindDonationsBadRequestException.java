package com.socially.donation.find.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class FindDonationsBadRequestException extends RuntimeException {

  public FindDonationsBadRequestException(String message) {
    super(message);
  }
}
