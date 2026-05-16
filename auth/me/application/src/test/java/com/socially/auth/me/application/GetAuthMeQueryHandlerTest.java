package com.socially.auth.me.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserEmailMapper;
import com.socially.auth.me.application.exception.MeUserNotFoundException;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
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

  @Mock private FindUserByEmailUseCase findUserByEmailUseCase;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  @Mock private AuthUserEmailMapper authUserEmailMapper;

  @InjectMocks private GetAuthMeQueryHandler handler;

  @Test
  void execute_should_throw_when_principal_is_not_jwt_authentication_token() {
    UnauthenticatedRequestException exception =
        assertThrows(
            UnauthenticatedRequestException.class, () -> handler.execute(() -> "anonymous"));

    assertEquals("Unauthenticated request", exception.getMessage());
  }

  @Test
  void execute_should_call_auth_user_email_mapper_with_current_jwt() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "me@example.com"));
    User user = sampleUser();
    when(authUserEmailMapper.resolveEmail(same(jwt))).thenReturn("me@example.com");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery("me@example.com")))
        .thenReturn(Optional.of(user));
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authUserEmailMapper).resolveEmail(same(jwt));
  }

  @Test
  void execute_should_call_find_by_email_with_email_from_jwt_resolution() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "me@example.com"));
    User user = sampleUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("me@example.com");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery("me@example.com")))
        .thenReturn(Optional.of(user));
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(findUserByEmailUseCase).execute(new FindUserByEmailQuery("me@example.com"));
  }

  @Test
  void execute_should_throw_me_user_not_found_when_user_missing() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "missing@example.com"));
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("missing@example.com");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery("missing@example.com")))
        .thenReturn(Optional.empty());

    assertThrows(
        MeUserNotFoundException.class, () -> handler.execute(new JwtAuthenticationToken(jwt)));
  }

  @Test
  void execute_should_call_find_donor_by_user_id_with_user_id_string() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "me@example.com"));
    User user = sampleUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("me@example.com");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery("me@example.com")))
        .thenReturn(Optional.of(user));
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(findDonorByUserIdUseCase)
        .execute(new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000"));
  }

  @Test
  void execute_should_return_empty_donor_when_no_donor_profile() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "me@example.com"));
    User user = sampleUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("me@example.com");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery("me@example.com")))
        .thenReturn(Optional.of(user));
    when(findDonorByUserIdUseCase.execute(
            new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(Optional.empty());

    AuthMeQueryResult result = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(user, result.user());
    assertTrue(result.donor().isEmpty());
  }

  @Test
  void execute_should_return_donor_when_profile_present() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "me@example.com"));
    User user = sampleUser();
    Donor donor = sampleDonor();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("me@example.com");
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery("me@example.com")))
        .thenReturn(Optional.of(user));
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

  private static Map<String, Object> baseClaims(String sub, String email) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", sub);
    claims.put("email", email);
    claims.put("given_name", "Jane");
    claims.put("family_name", "Doe");
    return claims;
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt("token-value", ISSUED_AT, EXPIRES_AT, Map.of("alg", "none"), claims);
  }
}
