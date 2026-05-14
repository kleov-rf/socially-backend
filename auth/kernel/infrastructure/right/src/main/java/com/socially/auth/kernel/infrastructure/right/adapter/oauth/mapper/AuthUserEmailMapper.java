package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public final class AuthUserEmailMapper {

  private final OidcJsonPayloadClaims jsonPayloadClaims;

  public String resolveEmail(Jwt jwt) {
    return resolveFromClaimStrings(jwt::getClaimAsString);
  }

  public String resolveEmail(JsonNode payload) {
    return resolveFromClaimStrings(name -> jsonPayloadClaims.text(payload, name));
  }

  private String resolveFromClaimStrings(Function<String, String> claim) {
    String email = blankToNull(claim.apply("email"));
    if (email != null) {
      return email;
    }
    email = blankToNull(claim.apply("preferred_username"));
    if (email != null && email.contains("@")) {
      return email;
    }
    email = blankToNull(claim.apply("username"));
    if (email != null) {
      return email;
    }
    email = blankToNull(claim.apply("cognito:username"));
    if (email != null) {
      return email;
    }
    return blankToNull(claim.apply("preferred_username"));
  }

  private String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
