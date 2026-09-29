package com.socially.user.updateprofile.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;
import com.socially.user.updateprofile.domain.port.right.UpdateUserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileCommandHandlerTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private UpdateUserProfileRepository updateUserProfileRepository;

  @InjectMocks private UpdateUserProfileCommandHandler handler;

  @Test
  void execute_should_call_repository_update_profile_with_user_id_from_command() {
    var command =
        UpdateUserProfileCommand.create(USER_ID, "https://idp.example", "sub-1")
            .withEmail(Optional.of("user@example.com"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    handler.execute(command);

    ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(updateUserProfileRepository)
        .updateProfile(
            userIdCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq(Optional.of("user@example.com")),
            org.mockito.ArgumentMatchers.eq(Optional.of("Jane")),
            org.mockito.ArgumentMatchers.eq(Optional.of("Doe")));
    assertEquals(USER_ID, userIdCaptor.getValue());
  }

  @Test
  void execute_should_call_repository_update_profile_with_issuer_from_command() {
    var command =
        UpdateUserProfileCommand.create(USER_ID, "https://idp.example", "sub-1")
            .withEmail(Optional.of("user@example.com"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    handler.execute(command);

    ArgumentCaptor<String> issuerCaptor = ArgumentCaptor.forClass(String.class);
    verify(updateUserProfileRepository)
        .updateProfile(
            org.mockito.ArgumentMatchers.eq(USER_ID),
            issuerCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq(Optional.of("user@example.com")),
            org.mockito.ArgumentMatchers.eq(Optional.of("Jane")),
            org.mockito.ArgumentMatchers.eq(Optional.of("Doe")));
    assertEquals("https://idp.example", issuerCaptor.getValue());
  }

  @Test
  void execute_should_pass_empty_profile_fields_when_command_values_are_blank() {
    var command =
        UpdateUserProfileCommand.create(USER_ID, "https://idp.example", "sub-1")
            .withEmail(Optional.of("  "))
            .withGivenName(Optional.of(" "))
            .withFamilyName(Optional.of(" "));

    handler.execute(command);

    verify(updateUserProfileRepository)
        .updateProfile(
            org.mockito.ArgumentMatchers.eq(USER_ID),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq(Optional.empty()),
            org.mockito.ArgumentMatchers.eq(Optional.empty()),
            org.mockito.ArgumentMatchers.eq(Optional.empty()));
  }

  @Test
  void execute_should_throw_exception_when_user_id_is_blank() {
    var command = UpdateUserProfileCommand.create("   ", "https://idp.example", "sub-1");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("userId cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_issuer_is_blank() {
    var command = UpdateUserProfileCommand.create(USER_ID, "   ", "sub-1");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("issuer cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_subject_is_blank() {
    var command = UpdateUserProfileCommand.create(USER_ID, "https://idp.example", "   ");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("subject cannot be blank", exception.getMessage());
  }
}
