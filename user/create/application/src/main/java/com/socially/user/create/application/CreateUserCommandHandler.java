package com.socially.user.create.application;

import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.port.left.CreateDonorUseCase;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.input.mapper.CreateUserCommandMapper;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.create.domain.port.right.CreateUserRepository;
import com.socially.user.kernel.domain.entity.User;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class CreateUserCommandHandler implements CreateUserUseCase {

  private final CreateUserCommandMapper createUserCommandMapper;
  private final Clock clock;
  private final CreateUserRepository userRepository;
  private final CreateDonorUseCase createDonorUseCase;

  @Override
  public User execute(CreateUserCommand command) {
    User user = createUserCommandMapper.toDomain(command, clock.instant());
    userRepository.create(user);

    createDonorUseCase.execute(
        new CreateDonorCommand(
            user.id().value().toString(),
            user.email().value(),
            user.givenName(),
            user.familyName()));

    return user;
  }
}
