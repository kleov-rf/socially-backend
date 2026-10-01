package com.socially.user.me.infrastructure.left.adapter.http.me.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UserMeProfilesDto(
    Optional<UserMeDonorProfileDto> donor, Optional<Object> organization) {

  public static UserMeProfilesDto create() {
    return new UserMeProfilesDto(Optional.empty(), Optional.empty());
  }

  public UserMeProfilesDto withDonor(Optional<UserMeDonorProfileDto> donor) {
    return new UserMeProfilesDto(donor, organization);
  }

  public UserMeProfilesDto withOrganization(Optional<Object> organization) {
    return new UserMeProfilesDto(donor, organization);
  }
}
