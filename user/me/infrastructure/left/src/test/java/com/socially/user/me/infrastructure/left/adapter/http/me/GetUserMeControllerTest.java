package com.socially.user.me.infrastructure.left.adapter.http.me;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.me.application.output.UserMeQueryResult;
import com.socially.user.me.application.port.left.GetUserMeUseCase;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeDonorProfileDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeProfilesDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeResponse;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeUserDto;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.mapper.UserMeResponseMapper;
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
class GetUserMeControllerTest {

  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private GetUserMeUseCase useCase;
  @Mock private UserMeResponseMapper userMeResponseMapper;

  @InjectMocks private GetUserMeController sut;

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
    UserMeQueryResult result = new UserMeQueryResult(user, Optional.empty());
    when(useCase.execute(principal)).thenReturn(result);
    when(userMeResponseMapper.toResponse(result))
        .thenReturn(
            new UserMeResponse(
                new UserMeUserDto("550e8400-e29b-41d4-a716-446655440000", "e@x.com"),
                new UserMeProfilesDto(null, null)));

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
    UserMeQueryResult result = new UserMeQueryResult(user, Optional.empty());
    when(useCase.execute(principal)).thenReturn(result);
    UserMeResponse mapped =
        new UserMeResponse(
            new UserMeUserDto("550e8400-e29b-41d4-a716-446655440001", "a@b.com"),
            new UserMeProfilesDto(null, null));
    when(userMeResponseMapper.toResponse(same(result))).thenReturn(mapped);

    sut.me(principal);

    verify(userMeResponseMapper).toResponse(same(result));
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
    UserMeQueryResult result = new UserMeQueryResult(user, Optional.empty());
    when(useCase.execute(principal)).thenReturn(result);
    when(userMeResponseMapper.toResponse(result))
        .thenReturn(
            new UserMeResponse(
                new UserMeUserDto("550e8400-e29b-41d4-a716-446655440002", "e@x.com"),
                new UserMeProfilesDto(null, null)));

    ResponseEntity<UserMeResponse> response = sut.me(principal);

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
    UserMeQueryResult result = new UserMeQueryResult(user, Optional.empty());
    UserMeResponse mapped =
        new UserMeResponse(
            new UserMeUserDto("550e8400-e29b-41d4-a716-446655440003", "user@example.com"),
            new UserMeProfilesDto(
                new UserMeDonorProfileDto(
                    "660e8400-e29b-41d4-a716-446655440099", "user@example.com", "Jane", "Doe"),
                null));
    when(useCase.execute(principal)).thenReturn(result);
    when(userMeResponseMapper.toResponse(result)).thenReturn(mapped);

    ResponseEntity<UserMeResponse> response = sut.me(principal);

    assertEquals(mapped, response.getBody());
  }
}
