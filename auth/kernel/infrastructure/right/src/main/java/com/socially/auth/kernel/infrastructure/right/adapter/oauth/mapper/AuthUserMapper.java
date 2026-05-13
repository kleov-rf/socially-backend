package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.auth.kernel.domain.AuthUser;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public final class AuthUserMapper {

  private final ObjectMapper objectMapper;

  public AuthUser fromIdToken(String idToken) {
    if (!StringUtils.hasText(idToken)) {
      return new AuthUser(null, null, null, null);
    }

    String[] tokenParts = idToken.split("\\.");
    if (tokenParts.length < 2) {
      throw new IllegalArgumentException("Invalid id_token format");
    }

    try {
      byte[] decodedPayload = Base64.getUrlDecoder().decode(tokenParts[1]);
      JsonNode payload = objectMapper.readTree(decodedPayload);

      String id = payload.path("sub").asText(null);
      String email = payload.path("email").asText(null);
      String givenName = payload.path("given_name").asText(null);
      String familyName = payload.path("family_name").asText(null);

      return new AuthUser(id, email, givenName, familyName);
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unable to decode id_token payload", exception);
    }
  }
}
