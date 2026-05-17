package com.socially.donor.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public final class DonorNotFoundAfterCreateException extends RuntimeException {
  public DonorNotFoundAfterCreateException() {
    super("Donor not found after create");
  }
}
