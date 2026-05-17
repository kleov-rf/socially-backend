package com.socially.auth.me.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.AuthenticatedUserNotFoundException;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserFromJwtMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.AuthenticatedUserResolver;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
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
class GetAuthMeQueryHandlerTest {

  private static final Instant ISSUED_AT = Instant.parse("2024-01-01T00:00:00Z");
  private static final Instant EXPIRES_AT = Instant.parse("2024-01-01T01:00:00Z");
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final AuthUser AUTH_USER =
      new AuthUser("https://idp.example", "sub-99", "me@example.com", "Jane", "Doe");

  @Mock private AuthenticatedUserResolver authenticatedUserResolver;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  @Mock private AuthUserFromJwtMapper authUserFromJwtMapper;

  @InjectMocks private GetAuthMeQueryHandler handler;

  @Test
  void execute_should_throw_when_principal_is_not_jwt_authentication_token() {
    UnauthenticatedRequestException exception =
        assertThrows(
            UnauthenticatedRequestException.class, () -> handler.execute(() -> "anonymous"));

    assertEquals("Unauthenticated request", exception.getMessage());
  }

  @Test
  void execute_should_call_auth_user_from_jwt_mapper_with_current_jwt() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User user = sampleUser();
    when(authUserFromJwtMapper.fromJwt(same(jwt))).thenReturn(AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authUserFromJwtMapper).fromJwt(same(jwt));
  }

  @Test
  void execute_should_call_authenticated_user_resolver_resolve_existing_with_mapped_auth_user() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User user = sampleUser();
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(eq(AUTH_USER))).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authenticatedUserResolver).resolveExisting(AUTH_USER);
  }

  @Test
  void execute_should_throw_authenticated_user_not_found_when_resolver_reports_missing_user() {
    Jwt jwt = jwtWithClaims(baseClaims());
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER))
        .thenThrow(new AuthenticatedUserNotFoundException());

    AuthenticatedUserNotFoundException exception =
        assertThrows(
            AuthenticatedUserNotFoundException.class,
            () -> handler.execute(new JwtAuthenticationToken(jwt)));

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void execute_should_call_find_donor_by_user_id_with_user_id_string() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User user = sampleUser();
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(findDonorByUserIdUseCase)
        .execute(new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000"));
  }

  @Test
  void execute_should_return_empty_donor_when_no_donor_profile() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User user = sampleUser();
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    AuthMeQueryResult result = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(user, result.user());
    assertTrue(result.donor().isEmpty());
  }

  @Test
  void execute_should_return_donor_when_profile_present() {
    Jwt jwt = jwtWithClaims(baseClaims());
    User user = sampleUser();
    Donor donor = sampleDonor();
    when(authUserFromJwtMapper.fromJwt(any(Jwt.class))).thenReturn(AUTH_USER);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(
            eq(new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000"))))
        .thenReturn(Optional.of(donor));

    AuthMeQueryResult result = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(Optional.of(donor), result.donor());
  }

  private static User sampleUser() {
    return User.create(
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        Email.from("me@example.com"),
        "Jane",
        "Doe",
        CREATED_AT);
  }

  private static Donor sampleDonor() {
    return Donor.create(
        Id.from("660e8400-e29b-41d4-a716-446655440001"),
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        "me@example.com",
        "Jane",
        "Doe",
        CREATED_AT);
  }

  private static Map<String, Object> baseClaims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("iss", "https://idp.example");
    claims.put("sub", "sub-99");
    claims.put("email", "me@example.com");
    claims.put("given_name", "Jane");
    claims.put("family_name", "Doe");
    return claims;
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt("token-value", ISSUED_AT, EXPIRES_AT, Map.of("alg", "none"), claims);
  }
}
