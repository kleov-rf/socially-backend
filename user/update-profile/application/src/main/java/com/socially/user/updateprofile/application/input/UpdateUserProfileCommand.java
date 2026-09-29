package com.socially.user.updateprofile.application.input;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateUserProfileCommand(
    String userId,
    String issuer,
    String subject,
    Optional<String> email,
    Optional<String> givenName,
    Optional<String> familyName) {

  public static UpdateUserProfileCommand create(String userId, String issuer, String subject) {
    return new UpdateUserProfileCommand(
        userId, issuer, subject, Optional.empty(), Optional.empty(), Optional.empty());
  }

  public UpdateUserProfileCommand withEmail(Optional<String> email) {
    return new UpdateUserProfileCommand(userId, issuer, subject, email, givenName, familyName);
  }

  public UpdateUserProfileCommand withGivenName(Optional<String> givenName) {
    return new UpdateUserProfileCommand(userId, issuer, subject, email, givenName, familyName);
  }

  public UpdateUserProfileCommand withFamilyName(Optional<String> familyName) {
    return new UpdateUserProfileCommand(userId, issuer, subject, email, givenName, familyName);
  }
}
