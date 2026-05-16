package com.socially.user.create.application.port.left;

import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.kernel.domain.entity.User;

public interface CreateUserUseCase {
  User execute(CreateUserCommand command);
}
