package com.socially.user.create.application.port.left;

import com.socially.user.create.application.input.CreateUserCommand;

public interface CreateUserUseCase {
  void execute(CreateUserCommand command);
}
