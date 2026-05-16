package com.socially.user.create.application;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.input.mapper.CreateUserCommandMapper;
import com.socially.user.create.domain.port.right.CreateUserRepository;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandHandlerTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private CreateUserRepository userRepository;
  @Mock private CreateUserCommandMapper createUserCommandMapper;
  @Mock private FindUserByEmailUseCase findUserByEmailUseCase;
  @Mock private Clock clock;

  @InjectMocks private CreateUserCommandHandler handler;

  @Test
  void execute_should_call_find_user_by_email_with_command_email() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(command.email())))
        .thenReturn(Optional.empty());

    handler.execute(command);

    verify(findUserByEmailUseCase).execute(new FindUserByEmailQuery(command.email()));
  }

  @Test
  void execute_should_not_call_repository_create_when_user_already_exists() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");
    User existingUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(command.email())))
        .thenReturn(Optional.of(existingUser));

    handler.execute(command);

    verify(userRepository, never()).create(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void execute_should_not_call_command_mapper_when_user_already_exists() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");
    User existingUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(command.email())))
        .thenReturn(Optional.of(existingUser));

    handler.execute(command);

    verify(createUserCommandMapper, never())
        .toDomain(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void execute_should_call_mapper_with_received_command() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");
    User mappedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(command.email())))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    verify(createUserCommandMapper).toDomain(command, CREATED_AT);
  }

  @Test
  void execute_should_call_mapper_with_clock_instant() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");
    User mappedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(command.email())))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    verify(clock).instant();
  }

  @Test
  void execute_should_call_repository_create_with_mapped_user() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");
    User mappedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(command.email())))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    verify(userRepository).create(mappedUser);
    verifyNoMoreInteractions(userRepository);
  }
}
