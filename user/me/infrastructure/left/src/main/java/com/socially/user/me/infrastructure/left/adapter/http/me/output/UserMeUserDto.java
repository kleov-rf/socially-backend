package com.socially.user.me.infrastructure.left.adapter.http.me.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UserMeUserDto(String id, String email, Optional<String> name) {

  public static UserMeUserDto create(String id, String email) {
    return new UserMeUserDto(id, email, Optional.empty());
  }

  public UserMeUserDto withName(Optional<String> name) {
    return new UserMeUserDto(id, email, name);
  }
}
