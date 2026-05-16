package com.socially.donor.kernel.domain.exception;

public final class DonorNotFoundAfterCreateException extends RuntimeException {
  public DonorNotFoundAfterCreateException() {
    super("Donor not found after create");
  }
}
