package com.socially.auth.me.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class GetCurrentAuthUserQueryHandlerTest {

  private static final Instant ISSUED_AT = Instant.parse("2024-01-01T00:00:00Z");
  private static final Instant EXPIRES_AT = Instant.parse("2024-01-01T01:00:00Z");

  private final GetCurrentAuthUserQueryHandler handler = new GetCurrentAuthUserQueryHandler();

  @Test
  void execute_should_throw_exception_when_principal_is_not_jwt_authentication_token() {
    UnauthenticatedRequestException exception =
        assertThrows(
            UnauthenticatedRequestException.class, () -> handler.execute(() -> "anonymous"));

    assertEquals("Unauthenticated request", exception.getMessage());
  }

  @Test
  void execute_should_return_user_with_id() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));

    AuthUser user = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals("sub-99", user.id());
  }

  @Test
  void execute_should_return_user_with_email() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-1", "email-test@example.com"));

    AuthUser user = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals("email-test@example.com", user.email());
  }

  static Stream<Arguments> fullNameCases() {
    return Stream.of(
        Arguments.of("User", "Name", "User Name"),
        Arguments.of("User", null, "User"),
        Arguments.of(null, "Name", "Name"),
        Arguments.of(null, null, null));
  }

  @ParameterizedTest
  @MethodSource("fullNameCases")
  void execute_should_return_user_with_full_name(
      String givenName, String familyName, String expectedName) {
    Map<String, Object> claims = baseClaims("s", "e@x.com");
    if (givenName != null) {
      claims.put("given_name", givenName);
    }
    if (familyName != null) {
      claims.put("family_name", familyName);
    }
    Jwt jwt = jwtWithClaims(claims);

    AuthUser user = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(expectedName, user.name());
  }

  private static Map<String, Object> baseClaims(String sub, String email) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", sub);
    claims.put("email", email);
    return claims;
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt("token-value", ISSUED_AT, EXPIRES_AT, Map.of("alg", "none"), claims);
  }
}
