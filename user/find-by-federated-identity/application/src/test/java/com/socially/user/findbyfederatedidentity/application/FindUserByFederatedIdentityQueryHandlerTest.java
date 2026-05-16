package com.socially.user.findbyfederatedidentity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.findbyfederatedidentity.application.input.FindUserByFederatedIdentityQuery;
import com.socially.user.findbyfederatedidentity.domain.port.right.FindUserByFederatedIdentityRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindUserByFederatedIdentityQueryHandlerTest {

  @Mock private FindUserByFederatedIdentityRepository findUserByFederatedIdentityRepository;

  @InjectMocks private FindUserByFederatedIdentityQueryHandler handler;

  @Test
  void execute_should_call_repository_with_issuer_and_subject_from_query() {
    var query = new FindUserByFederatedIdentityQuery("https://idp.example", "sub-1");
    when(findUserByFederatedIdentityRepository.findByIssuerAndSubject(
            "https://idp.example", "sub-1"))
        .thenReturn(Optional.empty());

    handler.execute(query);

    verify(findUserByFederatedIdentityRepository)
        .findByIssuerAndSubject("https://idp.example", "sub-1");
  }

  @Test
  void execute_should_return_result_from_repository_when_user_exists() {
    var query = new FindUserByFederatedIdentityQuery("https://idp.example", "sub-1");
    User foundUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    when(findUserByFederatedIdentityRepository.findByIssuerAndSubject(
            "https://idp.example", "sub-1"))
        .thenReturn(Optional.of(foundUser));

    Optional<User> result = handler.execute(query);

    assertEquals(Optional.of(foundUser), result);
  }

  @Test
  void execute_should_return_empty_when_repository_returns_empty() {
    var query = new FindUserByFederatedIdentityQuery("https://idp.example", "sub-1");
    when(findUserByFederatedIdentityRepository.findByIssuerAndSubject(
            "https://idp.example", "sub-1"))
        .thenReturn(Optional.empty());

    Optional<User> result = handler.execute(query);

    assertTrue(result.isEmpty());
  }

  @Test
  void execute_should_throw_exception_when_query_issuer_is_blank() {
    var query = new FindUserByFederatedIdentityQuery("   ", "sub-1");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(query));

    assertEquals("issuer cannot be blank", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_query_subject_is_blank() {
    var query = new FindUserByFederatedIdentityQuery("https://idp.example", "   ");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(query));

    assertEquals("subject cannot be blank", exception.getMessage());
  }
}
