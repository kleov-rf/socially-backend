package com.socially.auth.me.infrastructure.left.adapter.http.me;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.UserResponseDto;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.UserResponseDtoMapper;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.security.Principal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GetCurrentAuthUserControllerTest {

  @Mock private GetCurrentAuthUserUseCase useCase;
  @Mock private UserResponseDtoMapper userResponseDtoMapper;

  @InjectMocks private GetCurrentAuthUserController sut;

  @Test
  void me_should_call_use_case_with_received_principal() {
    Principal principal = () -> "ignored";
    when(useCase.execute(principal))
        .thenReturn(
            User.create(
                Id.from("550e8400-e29b-41d4-a716-446655440000"),
                Email.from("e@x.com"),
                "N",
                null,
                Instant.parse("2024-06-01T12:00:00Z")));

    sut.me(principal);

    verify(useCase).execute(principal);
  }

  @Test
  void me_should_call_response_mapper_with_retrieved_user_from_use_case() {
    Principal principal = () -> "ignored";
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001"),
            Email.from("a@b.com"),
            "Full",
            null,
            Instant.parse("2024-06-01T12:00:00Z"));
    when(useCase.execute(principal)).thenReturn(user);
    when(userResponseDtoMapper.toResponse(user))
        .thenReturn(new UserResponseDto("id-1", "a@b.com", "Full"));

    sut.me(principal);

    verify(userResponseDtoMapper).toResponse(user);
  }

  @Test
  void me_should_return_response_with_ok_status() {
    Principal principal = () -> "ignored";
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440002"),
            Email.from("e@x.com"),
            null,
            null,
            Instant.parse("2024-06-01T12:00:00Z"));
    when(useCase.execute(principal)).thenReturn(user);
    when(userResponseDtoMapper.toResponse(any(User.class)))
        .thenReturn(new UserResponseDto("id", "e@x.com", null));

    ResponseEntity<UserResponseDto> response = sut.me(principal);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void me_should_return_response_with_mapped_user_response() {
    Principal principal = () -> "ignored";
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440003"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    UserResponseDto mapped = new UserResponseDto("sub-x", "user@example.com", "Jane Doe");
    when(useCase.execute(principal)).thenReturn(user);
    when(userResponseDtoMapper.toResponse(user)).thenReturn(mapped);

    ResponseEntity<UserResponseDto> response = sut.me(principal);

    assertEquals(mapped, response.getBody());
  }
}
