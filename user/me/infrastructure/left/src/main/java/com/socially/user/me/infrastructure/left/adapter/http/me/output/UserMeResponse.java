package com.socially.user.me.infrastructure.left.adapter.http.me.output;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record UserMeResponse(UserMeUserDto user, UserMeProfilesDto profiles) {

  public static UserMeResponse create(UserMeUserDto user, UserMeProfilesDto profiles) {
    return new UserMeResponse(user, profiles);
  }
}
