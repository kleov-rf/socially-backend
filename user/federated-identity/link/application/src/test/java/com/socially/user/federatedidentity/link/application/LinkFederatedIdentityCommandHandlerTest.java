package com.socially.user.federatedidentity.link.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.socially.user.federatedidentity.link.application.input.LinkFederatedIdentityCommand;
import com.socially.user.federatedidentity.link.domain.port.right.LinkFederatedIdentityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkFederatedIdentityCommandHandlerTest {

  @Mock private LinkFederatedIdentityRepository linkFederatedIdentityRepository;

  @InjectMocks private LinkFederatedIdentityCommandHandler handler;

  @Test
  void execute_should_call_repository_link_with_user_id_from_command() {
    var command =
        new LinkFederatedIdentityCommand(
            "550e8400-e29b-41d4-a716-446655440000",
            "https://idp.example",
            "sub-1",
            "user@example.com");

    handler.execute(command);

    ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(linkFederatedIdentityRepository)
        .link(
            userIdCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq("user@example.com"));
    assertEquals("550e8400-e29b-41d4-a716-446655440000", userIdCaptor.getValue());
  }

  @Test
  void execute_should_call_repository_link_with_issuer_from_command() {
    var command =
        new LinkFederatedIdentityCommand(
            "550e8400-e29b-41d4-a716-446655440000",
            "https://idp.example",
            "sub-1",
            "user@example.com");

    handler.execute(command);

    ArgumentCaptor<String> issuerCaptor = ArgumentCaptor.forClass(String.class);
    verify(linkFederatedIdentityRepository)
        .link(
            org.mockito.ArgumentMatchers.eq("550e8400-e29b-41d4-a716-446655440000"),
            issuerCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.eq("user@example.com"));
    assertEquals("https://idp.example", issuerCaptor.getValue());
  }

  @Test
  void execute_should_call_repository_link_with_subject_from_command() {
    var command =
        new LinkFederatedIdentityCommand(
            "550e8400-e29b-41d4-a716-446655440000",
            "https://idp.example",
            "sub-1",
            "user@example.com");

    handler.execute(command);

    ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
    verify(linkFederatedIdentityRepository)
        .link(
            org.mockito.ArgumentMatchers.eq("550e8400-e29b-41d4-a716-446655440000"),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            subjectCaptor.capture(),
            org.mockito.ArgumentMatchers.eq("user@example.com"));
    assertEquals("sub-1", subjectCaptor.getValue());
  }

  @Test
  void execute_should_pass_null_email_when_command_email_is_blank() {
    var command =
        new LinkFederatedIdentityCommand(
            "550e8400-e29b-41d4-a716-446655440000", "https://idp.example", "sub-1", "   ");

    handler.execute(command);

    verify(linkFederatedIdentityRepository)
        .link(
            org.mockito.ArgumentMatchers.eq("550e8400-e29b-41d4-a716-446655440000"),
            org.mockito.ArgumentMatchers.eq("https://idp.example"),
            org.mockito.ArgumentMatchers.eq("sub-1"),
            org.mockito.ArgumentMatchers.isNull());
  }

  @Test
  void execute_should_throw_exception_when_user_id_is_blank() {
    var command =
        new LinkFederatedIdentityCommand("   ", "https://idp.example", "sub-1", "a@b.com");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("userId cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_issuer_is_blank() {
    var command =
        new LinkFederatedIdentityCommand(
            "550e8400-e29b-41d4-a716-446655440000", "   ", "sub-1", "a@b.com");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("issuer cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_subject_is_blank() {
    var command =
        new LinkFederatedIdentityCommand(
            "550e8400-e29b-41d4-a716-446655440000", "https://idp.example", "   ", "a@b.com");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));

    assertEquals("subject cannot be blank", exception.getMessage());
  }
}
