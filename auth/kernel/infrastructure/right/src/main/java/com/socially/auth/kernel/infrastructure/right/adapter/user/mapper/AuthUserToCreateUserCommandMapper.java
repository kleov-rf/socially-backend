package com.socially.auth.kernel.infrastructure.right.adapter.user.mapper;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.create.application.input.CreateUserCommand;
import org.springframework.stereotype.Component;

@Component
public class AuthUserToCreateUserCommandMapper {

  public CreateUserCommand toCommand(AuthUser authUser) {
    return CreateUserCommand.create(Id.generate().value().toString(), authUser.email())
        .withGivenName(authUser.givenName())
        .withFamilyName(authUser.familyName());
  }
}
