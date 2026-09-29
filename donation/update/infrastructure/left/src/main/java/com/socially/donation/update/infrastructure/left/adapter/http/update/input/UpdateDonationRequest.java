package com.socially.donation.update.infrastructure.left.adapter.http.update.input;

import jakarta.validation.Valid;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationRequest(
    Optional<String> title,
    Optional<String> description,
    Optional<@Valid UpdateDonationLocationRequest> location) {

  public static UpdateDonationRequest create() {
    return new UpdateDonationRequest(Optional.empty(), Optional.empty(), Optional.empty());
  }

  public UpdateDonationRequest withTitle(Optional<String> title) {
    return new UpdateDonationRequest(title, description, location);
  }

  public UpdateDonationRequest withDescription(Optional<String> description) {
    return new UpdateDonationRequest(title, description, location);
  }

  public UpdateDonationRequest withLocation(
      Optional<@Valid UpdateDonationLocationRequest> location) {
    return new UpdateDonationRequest(title, description, location);
  }
}
