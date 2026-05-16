package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.auth.kernel.domain.AuthUser;
import java.util.Base64;
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
      return new AuthUser(null, null, null, null, null);
    }

    String[] tokenParts = idToken.split("\\.");
    if (tokenParts.length < 2) {
      throw new IllegalArgumentException("Invalid id_token format");
    }

    try {
      byte[] decodedPayload = Base64.getUrlDecoder().decode(tokenParts[1]);
      JsonNode payload = objectMapper.readTree(decodedPayload);

      return new AuthUser(
          blankToNull(jsonPayloadClaims.text(payload, "iss")),
          blankToNull(jsonPayloadClaims.text(payload, "sub")),
          authUserEmailMapper.resolveEmail(payload),
          blankToNull(jsonPayloadClaims.text(payload, "given_name")),
          blankToNull(jsonPayloadClaims.text(payload, "family_name")));
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unable to decode id_token payload", exception);
    }
  }

  private String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
