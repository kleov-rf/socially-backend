package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.UserResponseDto;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.UserResponseDtoMapper;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshSessionResponseMapperTest {

  @Mock private UserResponseDtoMapper userResponseDtoMapper;
  @InjectMocks private RefreshSessionResponseMapper sut;

  @Test
  void toResponse_should_map_result_access_token() {
    AuthResult result =
        new AuthResult("access-token", "Bearer", 60L, new AuthUser("i", "e", "n", "x"));
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals("access-token", response.accessToken());
  }

  @Test
  void toResponse_should_map_result_token_type() {
    AuthResult result = new AuthResult("a", "JWT", 60L, new AuthUser("i", "e", "n", "x"));
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals("JWT", response.tokenType());
  }

  @Test
  void toResponse_should_map_result_expires_in() {
    AuthResult result = new AuthResult("a", "Bearer", 999L, new AuthUser("i", "e", "n", "x"));
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals(999L, response.expiresIn());
  }

  @Test
  void toResponse_should_map_result_expires_in_when_expires_in_is_not_present() {
    AuthResult result = new AuthResult("a", "Bearer", null, new AuthUser("i", "e", "n", "x"));
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals(0L, response.expiresIn());
  }

  @Test
  void toResponse_should_call_user_response_mapper_with_received_user() {
    AuthResult result = new AuthResult("a", "Bearer", 60L, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    sut.toResponse(result, sampleUser());

    verify(userResponseDtoMapper).toResponse(sampleUser());
  }

  @Test
  void toResponse_should_map_result_user_id() {
    AuthResult result = new AuthResult("a", "Bearer", 60L, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.user().id());
  }

  @Test
  void toResponse_should_map_result_user_email() {
    AuthResult result = new AuthResult("a", "Bearer", 60L, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals("mail@test.com", response.user().email());
  }

  @Test
  void toResponse_should_map_result_user_name() {
    AuthResult result = new AuthResult("a", "Bearer", 60L, null);
    when(userResponseDtoMapper.toResponse(sampleUser())).thenReturn(sampleUserResponseDto());

    RefreshSessionResponse response = sut.toResponse(result, sampleUser());

    assertEquals("Full Name", response.user().name());
  }

  private User sampleUser() {
    return User.create(
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        Email.from("mail@test.com"),
        "Full",
        "Name",
        Instant.parse("2024-06-01T12:00:00Z"));
  }

  private UserResponseDto sampleUserResponseDto() {
    return new UserResponseDto(
        "550e8400-e29b-41d4-a716-446655440000", "mail@test.com", "Full Name");
  }
}
