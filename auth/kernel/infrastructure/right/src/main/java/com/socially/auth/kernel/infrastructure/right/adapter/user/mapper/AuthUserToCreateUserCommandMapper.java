package com.socially.auth.kernel.infrastructure.right.adapter.user.mapper;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.user.create.application.input.CreateUserCommand;
import org.springframework.stereotype.Component;

@Component
public class AuthUserToCreateUserCommandMapper {

  public CreateUserCommand toCommand(AuthUser authUser) {
    String fullName = authUser.name();
    if (fullName == null || fullName.isBlank()) {
      return new CreateUserCommand(authUser.email(), null, null);
    }

    String trimmed = fullName.trim();
    String[] parts = trimmed.split("\\s+", 2);
    String givenName = parts[0];
    String familyName = parts.length > 1 ? parts[1] : null;
    return new CreateUserCommand(authUser.email(), givenName, familyName);
  }
}
