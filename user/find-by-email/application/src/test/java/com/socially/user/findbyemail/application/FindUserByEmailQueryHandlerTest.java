package com.socially.user.findbyemail.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.domain.port.right.FindUserByEmailRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.domain.valueobject.Id;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindUserByEmailQueryHandlerTest {

  @Mock private FindUserByEmailRepository findUserByEmailRepository;

  @InjectMocks private FindUserByEmailQueryHandler handler;

  @Test
  void execute_should_call_repository_with_email_from_query() {
    var query = new FindUserByEmailQuery("user@example.com");
    when(findUserByEmailRepository.findByEmail(Email.from("user@example.com")))
        .thenReturn(Optional.empty());

    handler.execute(query);

    verify(findUserByEmailRepository).findByEmail(Email.from("user@example.com"));
  }

  @Test
  void execute_should_return_result_from_repository_when_user_exists() {
    var query = new FindUserByEmailQuery("user@example.com");
    User foundUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    when(findUserByEmailRepository.findByEmail(Email.from("user@example.com")))
        .thenReturn(Optional.of(foundUser));

    Optional<User> result = handler.execute(query);

    assertEquals(Optional.of(foundUser), result);
  }

  @Test
  void execute_should_return_empty_when_repository_returns_empty() {
    var query = new FindUserByEmailQuery("user@example.com");
    when(findUserByEmailRepository.findByEmail(Email.from("user@example.com")))
        .thenReturn(Optional.empty());

    Optional<User> result = handler.execute(query);

    assertTrue(result.isEmpty());
  }

  @Test
  void execute_should_throw_exception_when_query_email_is_blank() {
    var query = new FindUserByEmailQuery("   ");

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> handler.execute(query));

    assertEquals("email cannot be blank", exception.getMessage());
  }
}
