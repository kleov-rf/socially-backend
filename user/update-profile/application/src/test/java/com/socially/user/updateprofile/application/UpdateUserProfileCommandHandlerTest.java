package com.socially.user.updateprofile.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;
import com.socially.user.updateprofile.domain.port.right.UpdateUserProfileRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileCommandHandlerTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private UpdateUserProfileRepository updateUserProfileRepository;

  @InjectMocks private UpdateUserProfileCommandHandler handler;

  @Test
  void execute_should_call_repository_update_profile_with_user_id_from_command() {
    var command =
        new UpdateUserProfileCommand(
            USER_ID, "https://idp.example", "sub-1", "user@example.com", "Jane", "Doe");
    User updatedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(updateUserProfileRepository.findById(USER_ID)).thenReturn(updatedUser);

    handler.execute(command);

    ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(updateUserProfileRepository)
        .updateProfile(
            userIdCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq("user@example.com"),
            org.mockito.ArgumentMatchers.eq("Jane"),
            org.mockito.ArgumentMatchers.eq("Doe"));
    assertEquals(USER_ID, userIdCaptor.getValue());
  }

  @Test
  void execute_should_call_repository_update_profile_with_issuer_from_command() {
    var command =
        new UpdateUserProfileCommand(
            USER_ID, "https://idp.example", "sub-1", "user@example.com", "Jane", "Doe");
    User updatedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(updateUserProfileRepository.findById(USER_ID)).thenReturn(updatedUser);

    handler.execute(command);

    ArgumentCaptor<String> issuerCaptor = ArgumentCaptor.forClass(String.class);
    verify(updateUserProfileRepository)
        .updateProfile(
            org.mockito.ArgumentMatchers.eq(USER_ID),
            issuerCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq("user@example.com"),
            org.mockito.ArgumentMatchers.eq("Jane"),
            org.mockito.ArgumentMatchers.eq("Doe"));
    assertEquals("https://idp.example", issuerCaptor.getValue());
  }

  @Test
  void execute_should_call_repository_find_by_id_with_user_id_from_command() {
    var command =
        new UpdateUserProfileCommand(
            USER_ID, "https://idp.example", "sub-1", "user@example.com", "Jane", "Doe");
    User updatedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(updateUserProfileRepository.findById(USER_ID)).thenReturn(updatedUser);

    User result = handler.execute(command);

    verify(updateUserProfileRepository).findById(USER_ID);
    assertEquals(updatedUser, result);
  }

  @Test
  void execute_should_pass_null_profile_fields_when_command_values_are_blank() {
    var command = new UpdateUserProfileCommand(USER_ID, "https://idp.example", "sub-1", "  ", " ", " ");
    User updatedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(updateUserProfileRepository.findById(USER_ID)).thenReturn(updatedUser);

    handler.execute(command);

    verify(updateUserProfileRepository)
        .updateProfile(
            org.mockito.ArgumentMatchers.eq(USER_ID),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.isNull(),
            org.mockito.ArgumentMatchers.isNull(),
            org.mockito.ArgumentMatchers.isNull());
  }

  @Test
  void execute_should_throw_exception_when_user_id_is_blank() {
    var command =
        new UpdateUserProfileCommand("   ", "https://idp.example", "sub-1", "a@b.com", "Jane", "Doe");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("userId cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_issuer_is_blank() {
    var command = new UpdateUserProfileCommand(USER_ID, "   ", "sub-1", "a@b.com", "Jane", "Doe");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("issuer cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_subject_is_blank() {
    var command =
        new UpdateUserProfileCommand(USER_ID, "https://idp.example", "   ", "a@b.com", "Jane", "Doe");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("subject cannot be blank", exception.getMessage());
  }
}
