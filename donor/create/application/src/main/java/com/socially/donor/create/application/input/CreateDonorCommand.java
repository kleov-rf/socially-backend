package com.socially.donor.create.application.input;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CreateDonorCommand(
    String userId, String email, Optional<String> givenName, Optional<String> familyName) {

  public static CreateDonorCommand create(String userId, String email) {
    return new CreateDonorCommand(userId, email, Optional.empty(), Optional.empty());
  }

  public CreateDonorCommand withGivenName(Optional<String> givenName) {
    return new CreateDonorCommand(userId, email, givenName, familyName);
  }

  public CreateDonorCommand withFamilyName(Optional<String> familyName) {
    return new CreateDonorCommand(userId, email, givenName, familyName);
  }
}
