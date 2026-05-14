package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AuthUserEmailMapperTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Mock private OidcJsonPayloadClaims jsonPayloadClaims;

  @InjectMocks private AuthUserEmailMapper sut;

  @Test
  void resolveEmail_fromJson_should_prefer_email_claim() throws JsonProcessingException {
    JsonNode payload =
        json("{\"email\":\"a@b.com\",\"username\":\"other\",\"preferred_username\":\"c@d.com\"}");
    when(jsonPayloadClaims.text(payload, "email")).thenReturn("a@b.com");

    String actual = sut.resolveEmail(payload);

    assertEquals("a@b.com", actual);
  }

  @Test
  void resolveEmail_fromJson_should_use_preferred_username_with_at_when_email_absent()
      throws JsonProcessingException {
    JsonNode payload = json("{\"preferred_username\":\"user@example.com\"}");
    when(jsonPayloadClaims.text(payload, "email")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "preferred_username")).thenReturn("user@example.com");

    String actual = sut.resolveEmail(payload);

    assertEquals("user@example.com", actual);
  }

  @Test
  void resolveEmail_fromJson_should_use_username_when_email_absent()
      throws JsonProcessingException {
    JsonNode payload = json("{\"username\":\"dev@socially.local\"}");
    when(jsonPayloadClaims.text(payload, "email")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "preferred_username")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "username")).thenReturn("dev@socially.local");

    String actual = sut.resolveEmail(payload);

    assertEquals("dev@socially.local", actual);
  }

  @Test
  void resolveEmail_fromJson_should_use_cognito_username_when_email_and_username_absent()
      throws JsonProcessingException {
    JsonNode payload = json("{\"cognito:username\":\"pool_sub|user@example.com\"}");
    when(jsonPayloadClaims.text(payload, "email")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "preferred_username")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "username")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "cognito:username"))
        .thenReturn("pool_sub|user@example.com");

    String actual = sut.resolveEmail(payload);

    assertEquals("pool_sub|user@example.com", actual);
  }

  @Test
  void resolveEmail_fromJson_should_fallback_to_preferred_username_without_at()
      throws JsonProcessingException {
    JsonNode payload = json("{\"preferred_username\":\"plainuser\"}");
    when(jsonPayloadClaims.text(payload, "email")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "preferred_username")).thenReturn("plainuser");
    when(jsonPayloadClaims.text(payload, "username")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "cognito:username")).thenReturn(null);

    String actual = sut.resolveEmail(payload);

    assertEquals("plainuser", actual);
  }

  @Test
  void resolveEmail_fromJson_should_return_null_when_no_claims() throws JsonProcessingException {
    JsonNode payload = json("{}");
    when(jsonPayloadClaims.text(payload, "email")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "preferred_username")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "username")).thenReturn(null);
    when(jsonPayloadClaims.text(payload, "cognito:username")).thenReturn(null);

    String actual = sut.resolveEmail(payload);

    assertNull(actual);
  }

  @Test
  void resolveEmail_fromJwt_should_use_username_when_email_absent() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", "dev@socially.local");
    Jwt jwt =
        new Jwt(
            "token-value",
            Instant.parse("2024-01-01T00:00:00Z"),
            Instant.parse("2024-01-01T01:00:00Z"),
            Map.of("alg", "none"),
            claims);

    String actual = sut.resolveEmail(jwt);

    assertEquals("dev@socially.local", actual);
    verifyNoInteractions(jsonPayloadClaims);
  }

  private static JsonNode json(String raw) throws JsonProcessingException {
    return OBJECT_MAPPER.readTree(raw);
  }
}
