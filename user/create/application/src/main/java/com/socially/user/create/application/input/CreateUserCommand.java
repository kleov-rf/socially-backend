package com.socially.user.create.application.input;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record CreateUserCommand(
    String userId, String email, Optional<String> givenName, Optional<String> familyName) {

  public static CreateUserCommand create(String userId, String email) {
    return new CreateUserCommand(userId, email, Optional.empty(), Optional.empty());
  }

  public CreateUserCommand withGivenName(Optional<String> givenName) {
    return new CreateUserCommand(userId, email, givenName, familyName);
  }

  public CreateUserCommand withFamilyName(Optional<String> familyName) {
    return new CreateUserCommand(userId, email, givenName, familyName);
  }
}
