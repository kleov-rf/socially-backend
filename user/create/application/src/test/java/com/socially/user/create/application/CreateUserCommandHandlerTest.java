package com.socially.user.create.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.port.left.CreateDonorUseCase;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.input.mapper.CreateUserCommandMapper;
import com.socially.user.create.domain.port.right.CreateUserRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandHandlerTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private CreateUserRepository userRepository;
  @Mock private CreateUserCommandMapper createUserCommandMapper;
  @Mock private Clock clock;
  @Mock private CreateDonorUseCase createDonorUseCase;

  @InjectMocks private CreateUserCommandHandler handler;

  private static CreateUserCommand sampleCommand() {
    return CreateUserCommand.create(USER_ID, "user@example.com")
        .withGivenName(Optional.of("Jane"))
        .withFamilyName(Optional.of("Doe"));
  }

  private static User sampleUser() {
    return User.create(Id.from(USER_ID), Email.from("user@example.com"), CREATED_AT)
        .withGivenName(Optional.of("Jane"))
        .withFamilyName(Optional.of("Doe"));
  }

  @Test
  void execute_should_call_mapper_with_received_command() {
    var command = sampleCommand();
    User mappedUser = sampleUser();
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    verify(createUserCommandMapper).toDomain(command, CREATED_AT);
  }

  @Test
  void execute_should_call_mapper_with_clock_instant() {
    var command = sampleCommand();
    User mappedUser = sampleUser();
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    verify(clock).instant();
  }

  @Test
  void execute_should_call_repository_create_with_mapped_user() {
    var command = sampleCommand();
    User mappedUser = sampleUser();
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    verify(userRepository).create(mappedUser);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void execute_should_call_create_donor_after_repository_create() {
    var command = sampleCommand();
    User mappedUser = sampleUser();
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    var inOrder = inOrder(userRepository, createDonorUseCase);
    inOrder.verify(userRepository).create(mappedUser);
    inOrder
        .verify(createDonorUseCase)
        .execute(org.mockito.ArgumentMatchers.any(CreateDonorCommand.class));
  }

  @Test
  void execute_should_call_create_donor_with_mapped_user_id_email_and_names() {
    var command = sampleCommand();
    User mappedUser = sampleUser();
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createUserCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedUser);

    handler.execute(command);

    ArgumentCaptor<CreateDonorCommand> donorCommandCaptor =
        ArgumentCaptor.forClass(CreateDonorCommand.class);
    verify(createDonorUseCase).execute(donorCommandCaptor.capture());
    CreateDonorCommand donorCommand = donorCommandCaptor.getValue();
    assertEquals(USER_ID, donorCommand.userId());
    assertEquals("user@example.com", donorCommand.email());
    assertEquals(Optional.of("Jane"), donorCommand.givenName());
    assertEquals(Optional.of("Doe"), donorCommand.familyName());
  }
}
