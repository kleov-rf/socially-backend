package com.socially.user.me.infrastructure.left.adapter.http.me.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UserMeDonorProfileDto(
    String id, String email, Optional<String> givenName, Optional<String> familyName) {

  public static UserMeDonorProfileDto create(String id, String email) {
    return new UserMeDonorProfileDto(id, email, Optional.empty(), Optional.empty());
  }

  public UserMeDonorProfileDto withGivenName(Optional<String> givenName) {
    return new UserMeDonorProfileDto(id, email, givenName, familyName);
  }

  public UserMeDonorProfileDto withFamilyName(Optional<String> familyName) {
    return new UserMeDonorProfileDto(id, email, givenName, familyName);
  }
}
