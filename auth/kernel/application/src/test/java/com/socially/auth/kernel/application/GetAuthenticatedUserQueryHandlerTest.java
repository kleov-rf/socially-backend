package com.socially.auth.kernel.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.AuthUnauthorizedException;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserFromJwtMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.AuthenticatedUserResolver;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class GetAuthenticatedUserQueryHandlerTest {

  private static final Instant ISSUED_AT = Instant.parse("2024-01-01T00:00:00Z");
  private static final Instant EXPIRES_AT = Instant.parse("2024-01-01T01:00:00Z");
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final AuthUser EXPECTED_AUTH_USER =
      AuthUser.create("https://idp.example", "sub-99", "id-test@example.com")
          .withGivenName(Optional.of("Jane"))
          .withFamilyName(Optional.of("Doe"));

  @Mock private AuthenticatedUserResolver authenticatedUserResolver;
  @Mock private AuthUserFromJwtMapper authUserFromJwtMapper;

  @InjectMocks private GetAuthenticatedUserQueryHandler handler;

  @Test
  void execute_should_call_auth_user_from_jwt_mapper_with_current_jwt() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User foundUser = foundUser();
    when(authUserFromJwtMapper.fromJwt(same(jwt))).thenReturn(EXPECTED_AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(EXPECTED_AUTH_USER)).thenReturn(foundUser);

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authUserFromJwtMapper).fromJwt(same(jwt));
  }

  @Test
  void execute_should_call_authenticated_user_resolver_with_mapped_auth_user() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User foundUser = foundUser();
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(EXPECTED_AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(eq(EXPECTED_AUTH_USER))).thenReturn(foundUser);

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authenticatedUserResolver).resolveExisting(EXPECTED_AUTH_USER);
  }

  @Test
  void execute_should_return_user_from_authenticated_user_resolver() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User foundUser = foundUser();
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(EXPECTED_AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(EXPECTED_AUTH_USER)).thenReturn(foundUser);

    User result = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(foundUser, result);
  }

  @Test
  void execute_should_throw_authenticated_user_not_found_when_resolver_reports_missing_user() {
    Jwt jwt = jwtWithClaims(baseClaims());
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(EXPECTED_AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(EXPECTED_AUTH_USER))
        .thenThrow(new AuthUnauthorizedException("User not found"));

    AuthUnauthorizedException exception =
        assertThrows(
            AuthUnauthorizedException.class,
            () -> handler.execute(new JwtAuthenticationToken(jwt)));

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_if_received_principal_is_not_auth_token() {
    AuthUnauthorizedException exception =
        assertThrows(AuthUnauthorizedException.class, () -> handler.execute(() -> "anonymous"));

    assertEquals("Unauthenticated request", exception.getMessage());
  }

  private static User foundUser() {
    return User.create(
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        Email.from("id-test@example.com"),
        "Jane",
        "Doe",
        CREATED_AT);
  }

  private static Map<String, Object> baseClaims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("iss", "https://idp.example");
    claims.put("sub", "sub-99");
    claims.put("email", "id-test@example.com");
    claims.put("given_name", "Jane");
    claims.put("family_name", "Doe");
    return claims;
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt("token-value", ISSUED_AT, EXPIRES_AT, Map.of("alg", "none"), claims);
  }
}
