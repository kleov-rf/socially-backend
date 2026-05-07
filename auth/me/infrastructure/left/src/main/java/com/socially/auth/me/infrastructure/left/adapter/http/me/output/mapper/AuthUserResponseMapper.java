package com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthUserResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthUserResponseMapper {

  public AuthUserResponse toResponse(AuthUser authUser) {
    return new AuthUserResponse(authUser.id(), authUser.email(), authUser.name());
  }
}
