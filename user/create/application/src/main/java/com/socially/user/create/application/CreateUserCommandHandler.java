package com.socially.user.create.application;

import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.input.mapper.CreateUserCommandMapper;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.create.domain.port.right.CreateUserRepository;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
import com.socially.user.kernel.domain.entity.User;
import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateUserCommandHandler implements CreateUserUseCase {

  private final FindUserByEmailUseCase findUserByEmailUseCase;
  private final CreateUserCommandMapper createUserCommandMapper;
  private final Clock clock;
  private final CreateUserRepository userRepository;

  @Override
  public void execute(CreateUserCommand command) {
    FindUserByEmailQuery query = new FindUserByEmailQuery(command.email());
    Optional<User> existingUser = findUserByEmailUseCase.execute(query);

    if (existingUser.isPresent()) {
      return;
    }

    User user = createUserCommandMapper.toDomain(command, clock.instant());
    userRepository.create(user);
  }
}
