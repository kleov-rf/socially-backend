package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.UserResponseDto;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.UserResponseDtoMapper;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.commons.kernel.domain.valueobject.Id;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthCallbackResponseMapperTest {

  @Mock private UserResponseDtoMapper userResponseDtoMapper;
  @InjectMocks private AuthCallbackResponseMapper sut;

  @Test
  void toResponse_should_map_access_token() {
    String accessToken = "test-access-token";
    AuthResult result = new AuthResult(accessToken, null, null, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals(accessToken, response.accessToken());
  }

  @Test
  void toResponse_should_map_token_type() {
    String tokenType = "Bearer";
    AuthResult result = new AuthResult(null, tokenType, null, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals(tokenType, response.tokenType());
  }

  @Test
  void toResponse_should_map_expires_in() {
    long expiresIn = 3600L;
    AuthResult result = new AuthResult(null, null, expiresIn, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals(expiresIn, response.expiresIn());
  }

  @Test
  void toResponse_should_map_expires_in_when_expires_in_is_not_present() {
    Long expiresIn = null;
    AuthResult result = new AuthResult(null, null, expiresIn, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals(0L, response.expiresIn());
  }

  @Test
  void toResponse_should_call_user_response_mapper_with_received_user() {
    AuthUser authUser = new AuthUser("user-123", "test@example.com", "Test", "User");
    AuthResult result = new AuthResult(null, null, null, authUser);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    sut.toResponse(result, sampleUser());

    verify(userResponseDtoMapper).toResponse(sampleUser());
  }

  @Test
  void toResponse_should_map_user_id() {
    AuthResult result = new AuthResult(null, null, null, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.user().id());
  }

  @Test
  void toResponse_should_map_user_email() {
    AuthResult result = new AuthResult(null, null, null, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals("test@example.com", response.user().email());
  }

  @Test
  void toResponse_should_map_user_name() {
    AuthResult result = new AuthResult(null, null, null, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    AuthCallbackResponse response = sut.toResponse(result, sampleUser());

    assertEquals("Test User", response.user().name());
  }

  private User sampleUser() {
    return User.create(
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        Email.from("test@example.com"),
        "Test",
        "User",
        Instant.parse("2024-06-01T12:00:00Z"));
  }

  private UserResponseDto sampleUserResponseDto() {
    return new UserResponseDto(
        "550e8400-e29b-41d4-a716-446655440000", "test@example.com", "Test User");
  }
}
