package com.socially.auth.me.infrastructure.left.adapter.http.me;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.auth.me.application.port.left.GetAuthMeUseCase;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeDonorProfileDto;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeProfilesDto;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeResponse;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeUserDto;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper.AuthMeResponseMapper;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GetCurrentAuthUserControllerTest {

  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private GetAuthMeUseCase useCase;
  @Mock private AuthMeResponseMapper authMeResponseMapper;

  @InjectMocks private GetCurrentAuthUserController sut;

  @Test
  void me_should_call_use_case_with_received_principal() {
    Principal principal = () -> "ignored";
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("e@x.com"),
            "N",
            null,
            CREATED_AT);
    AuthMeQueryResult result = new AuthMeQueryResult(user, Optional.empty());
    when(useCase.execute(principal)).thenReturn(result);
    when(authMeResponseMapper.toResponse(result))
        .thenReturn(
            new AuthMeResponse(
                new AuthMeUserDto("550e8400-e29b-41d4-a716-446655440000", "e@x.com"),
                new AuthMeProfilesDto(null, null)));

    sut.me(principal);

    verify(useCase).execute(principal);
  }

  @Test
  void me_should_call_response_mapper_with_query_result() {
    Principal principal = () -> "ignored";
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001"),
            Email.from("a@b.com"),
            "Full",
            null,
            CREATED_AT);
    AuthMeQueryResult result = new AuthMeQueryResult(user, Optional.empty());
    when(useCase.execute(principal)).thenReturn(result);
    AuthMeResponse mapped =
        new AuthMeResponse(
            new AuthMeUserDto("550e8400-e29b-41d4-a716-446655440001", "a@b.com"),
            new AuthMeProfilesDto(null, null));
    when(authMeResponseMapper.toResponse(same(result))).thenReturn(mapped);

    sut.me(principal);

    verify(authMeResponseMapper).toResponse(same(result));
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
            CREATED_AT);
    AuthMeQueryResult result = new AuthMeQueryResult(user, Optional.empty());
    when(useCase.execute(principal)).thenReturn(result);
    when(authMeResponseMapper.toResponse(result))
        .thenReturn(
            new AuthMeResponse(
                new AuthMeUserDto("550e8400-e29b-41d4-a716-446655440002", "e@x.com"),
                new AuthMeProfilesDto(null, null)));

    ResponseEntity<AuthMeResponse> response = sut.me(principal);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void me_should_return_response_body_from_mapper() {
    Principal principal = () -> "ignored";
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440003"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            CREATED_AT);
    AuthMeQueryResult result = new AuthMeQueryResult(user, Optional.empty());
    AuthMeResponse mapped =
        new AuthMeResponse(
            new AuthMeUserDto("550e8400-e29b-41d4-a716-446655440003", "user@example.com"),
            new AuthMeProfilesDto(
                new AuthMeDonorProfileDto(
                    "660e8400-e29b-41d4-a716-446655440099", "user@example.com", "Jane", "Doe"),
                null));
    when(useCase.execute(principal)).thenReturn(result);
    when(authMeResponseMapper.toResponse(result)).thenReturn(mapped);

    ResponseEntity<AuthMeResponse> response = sut.me(principal);

    assertEquals(mapped, response.getBody());
  }
}
