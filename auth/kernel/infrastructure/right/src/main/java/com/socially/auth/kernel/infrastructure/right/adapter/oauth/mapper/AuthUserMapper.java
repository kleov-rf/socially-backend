package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.auth.kernel.domain.AuthUser;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public final class AuthUserMapper {

  private final ObjectMapper objectMapper;
  private final OidcJsonPayloadClaims jsonPayloadClaims;
  private final AuthUserEmailMapper authUserEmailMapper;

  public AuthUser fromIdToken(String idToken) {
    if (!StringUtils.hasText(idToken)) {
      return AuthUser.create(null, null, null);
    }

    String[] tokenParts = idToken.split("\\.");
    if (tokenParts.length < 2) {
      throw new IllegalArgumentException("Invalid id_token format");
    }

    try {
      byte[] decodedPayload = Base64.getUrlDecoder().decode(tokenParts[1]);
      JsonNode payload = objectMapper.readTree(decodedPayload);

      return AuthUser.create(
              blankToNull(jsonPayloadClaims.text(payload, "iss")),
              blankToNull(jsonPayloadClaims.text(payload, "sub")),
              authUserEmailMapper.resolveEmail(payload))
          .withGivenName(blankToOptional(jsonPayloadClaims.text(payload, "given_name")))
          .withFamilyName(blankToOptional(jsonPayloadClaims.text(payload, "family_name")));
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unable to decode id_token payload", exception);
    }
  }

  private static Optional<String> blankToOptional(String value) {
    return StringUtils.hasText(value) ? Optional.of(value.trim()) : Optional.empty();
  }

  private String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
