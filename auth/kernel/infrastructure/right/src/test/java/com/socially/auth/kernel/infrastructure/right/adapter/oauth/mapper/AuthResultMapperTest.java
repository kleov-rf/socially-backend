package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthResultMapperTest {

  @Mock private AuthUserMapper authUserMapper;

  @InjectMocks private AuthResultMapper sut;

  @Test
  void toAuthResult_should_call_mapper_with_received_id_token() {
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", "refresh-1", "Bearer", 3600L);
    AuthUser user = new AuthUser("user-id-1", "user@example.com", "John", "Doe");
    when(authUserMapper.fromIdToken("id-token-1")).thenReturn(user);

    sut.toAuthResult(tokenResponse);

    verify(authUserMapper).fromIdToken("id-token-1");
  }

  @Test
  void toAuthResult_should_return_result_with_received_access_token() {
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", "refresh-1", "Bearer", 3600L);
    when(authUserMapper.fromIdToken("id-token-1")).thenReturn(new AuthUser(null, null, null, null));

    AuthResult result = sut.toAuthResult(tokenResponse);

    assertEquals("access-token-1", result.accessToken());
  }

  @Test
  void toAuthResult_should_return_result_with_received_token_type() {
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", "refresh-1", "JWT", 3600L);
    when(authUserMapper.fromIdToken("id-token-1")).thenReturn(new AuthUser(null, null, null, null));

    AuthResult result = sut.toAuthResult(tokenResponse);

    assertEquals("JWT", result.tokenType());
  }

  @Test
  void toAuthResult_should_return_result_with_received_expires_in() {
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", "refresh-1", "Bearer", 7200L);
    when(authUserMapper.fromIdToken("id-token-1")).thenReturn(new AuthUser(null, null, null, null));

    AuthResult result = sut.toAuthResult(tokenResponse);

    assertEquals(7200L, result.expiresIn());
  }

  @Test
  void toAuthResult_should_return_result_with_mapped_user() {
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", "refresh-1", "Bearer", 3600L);
    AuthUser user = new AuthUser("user-id-1", "user@example.com", "John", "Doe");
    when(authUserMapper.fromIdToken("id-token-1")).thenReturn(user);

    AuthResult result = sut.toAuthResult(tokenResponse);

    assertEquals(user, result.user());
  }
}
