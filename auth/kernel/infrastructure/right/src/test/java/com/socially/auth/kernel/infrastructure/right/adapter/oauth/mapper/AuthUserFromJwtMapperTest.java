package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
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

@ExtendWith(MockitoExtension.class)
class AuthUserFromJwtMapperTest {

  private static final Instant ISSUED_AT = Instant.parse("2024-01-01T00:00:00Z");
  private static final Instant EXPIRES_AT = Instant.parse("2024-01-01T01:00:00Z");

  @Mock private AuthUserEmailMapper authUserEmailMapper;

  @InjectMocks private AuthUserFromJwtMapper sut;

  @Test
  void fromJwt_should_map_issuer_claim() {
    Jwt jwt = jwtWithClaims(baseClaims());

    AuthUser authUser = sut.fromJwt(jwt);

    assertEquals("https://idp.example", authUser.issuer());
  }

  @Test
  void fromJwt_should_map_subject_claim() {
    Jwt jwt = jwtWithClaims(baseClaims());

    AuthUser authUser = sut.fromJwt(jwt);

    assertEquals("sub-99", authUser.subject());
  }

  @Test
  void fromJwt_should_map_email_from_auth_user_email_mapper() {
    Jwt jwt = jwtWithClaims(baseClaims());
    when(authUserEmailMapper.resolveEmail(same(jwt))).thenReturn("user@example.com");

    AuthUser authUser = sut.fromJwt(jwt);

    assertEquals("user@example.com", authUser.email());
    verify(authUserEmailMapper).resolveEmail(same(jwt));
  }

  @Test
  void fromJwt_should_map_given_name_claim() {
    Jwt jwt = jwtWithClaims(baseClaims());

    AuthUser authUser = sut.fromJwt(jwt);

    assertEquals(Optional.of("Jane"), authUser.givenName());
  }

  @Test
  void fromJwt_should_map_family_name_claim() {
    Jwt jwt = jwtWithClaims(baseClaims());

    AuthUser authUser = sut.fromJwt(jwt);

    assertEquals(Optional.of("Doe"), authUser.familyName());
  }

  @Test
  void fromJwt_should_return_null_issuer_when_claim_is_blank() {
    Map<String, Object> claims = baseClaims();
    claims.put("iss", "   ");
    Jwt jwt = jwtWithClaims(claims);

    AuthUser authUser = sut.fromJwt(jwt);

    assertNull(authUser.issuer());
  }

  private static Map<String, Object> baseClaims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("iss", "https://idp.example");
    claims.put("sub", "sub-99");
    claims.put("email", "user@example.com");
    claims.put("given_name", "Jane");
    claims.put("family_name", "Doe");
    return claims;
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt("token-value", ISSUED_AT, EXPIRES_AT, Map.of("alg", "none"), claims);
  }
}
