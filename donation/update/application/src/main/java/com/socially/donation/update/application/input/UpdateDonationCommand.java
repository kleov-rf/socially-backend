package com.socially.donation.update.application.input;

import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationCommand(
    @NotNull String id,
    @NotNull Principal principal,
    Optional<String> title,
    Optional<String> description,
    Optional<UpdateDonationLocationCommand> location) {

  public static UpdateDonationCommand create(String id, Principal principal) {
    return new UpdateDonationCommand(
        id, principal, Optional.empty(), Optional.empty(), Optional.empty());
  }

  public UpdateDonationCommand withTitle(Optional<String> title) {
    return new UpdateDonationCommand(id, principal, title, description, location);
  }

  public UpdateDonationCommand withDescription(Optional<String> description) {
    return new UpdateDonationCommand(id, principal, title, description, location);
  }

  public UpdateDonationCommand withLocation(Optional<UpdateDonationLocationCommand> location) {
    return new UpdateDonationCommand(id, principal, title, description, location);
  }
}
