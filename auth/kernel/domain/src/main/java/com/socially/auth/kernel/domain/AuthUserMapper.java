package com.socially.auth.kernel.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public final class AuthUserMapper {

  private final ObjectMapper objectMapper;

  public AuthUser fromIdToken(String idToken) {
    if (!StringUtils.hasText(idToken)) {
      return new AuthUser(null, null, null);
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

      String fullName = buildFullName(givenName, familyName);

      return new AuthUser(id, email, fullName);
    } catch (Exception exception) {
      throw new IllegalArgumentException("Unable to decode id_token payload", exception);
    }
  }

  private static @Nullable String buildFullName(String givenName, String familyName) {
    Stream<String> names = Stream.of(givenName, familyName);
    List<String> nonEmptyNames = names.filter(StringUtils::hasText).toList();

    if (nonEmptyNames.isEmpty()) {
      return null;
    }

    return String.join(" ", nonEmptyNames);
  }
}
