package com.socially.user.create.application.input.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public final class CreateUserCommandMapper {
  public User toDomain(CreateUserCommand command, Instant now) {
    return User.create(
        Id.from(command.userId()),
        Email.from(command.email()),
        command.givenName(),
        command.familyName(),
        now);
  }
}
