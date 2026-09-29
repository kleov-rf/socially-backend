package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.auth.kernel.domain.AuthUser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthUserMapperTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Mock private ObjectMapper mapper;
  @Mock private OidcJsonPayloadClaims jsonPayloadClaims;
  @Mock private AuthUserEmailMapper authUserEmailMapper;

  @InjectMocks private AuthUserMapper sut;

  @ParameterizedTest
  @CsvSource(
      value = {"null", "''", "'   '"},
      nullValues = "null")
  void fromIdToken_should_return_empty_user_when_id_token_not_present(String idToken)
      throws IOException {
    AuthUser user = sut.fromIdToken(idToken);

    assertNull(user.issuer());
    assertNull(user.subject());
    assertNull(user.email());
    assertEquals(Optional.empty(), user.givenName());
    assertEquals(Optional.empty(), user.familyName());
    verify(mapper, never()).readTree(any(byte[].class));
  }

  @Test
  void fromIdToken_should_throw_exception_when_second_token_part_is_not_present() {
    assertThrows(IllegalArgumentException.class, () -> sut.fromIdToken("header-only"));
  }

  @Test
  void fromIdToken_should_call_object_mapper_to_read_payload_tree() throws Exception {
    String payload = "{\"iss\":\"https://idp.example\",\"sub\":\"id-1\"}";
    String token = createTokenWithPayload(payload);
    JsonNode tree = OBJECT_MAPPER.readTree(payload);
    when(mapper.readTree(any(byte[].class))).thenReturn(tree);
    when(jsonPayloadClaims.text(same(tree), eq("iss"))).thenReturn("https://idp.example");
    when(jsonPayloadClaims.text(same(tree), eq("sub"))).thenReturn("id-1");
    when(authUserEmailMapper.resolveEmail(same(tree))).thenReturn(null);
    when(jsonPayloadClaims.text(same(tree), eq("given_name"))).thenReturn(null);
    when(jsonPayloadClaims.text(same(tree), eq("family_name"))).thenReturn(null);

    sut.fromIdToken(token);

    verify(mapper).readTree(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
  }

  @Test
  void fromIdToken_should_return_auth_user_issuer() throws IOException {
    String idToken =
        createTokenWithPayload(
            parseValuesIntoJson(
                "https://idp.example", "sub-123", "email@example.com", "John", "Doe"));
    JsonNode payload = stubPayloadFromTokenJson(idToken);

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals("https://idp.example", user.issuer());
    verify(jsonPayloadClaims).text(same(payload), eq("iss"));
  }

  @Test
  void fromIdToken_should_return_auth_user_subject() throws IOException {
    String idToken =
        createTokenWithPayload(
            parseValuesIntoJson(
                "https://idp.example", "sub-123", "email@example.com", "John", "Doe"));
    JsonNode payload = stubPayloadFromTokenJson(idToken);

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals("sub-123", user.subject());
    verify(jsonPayloadClaims).text(same(payload), eq("sub"));
  }

  @Test
  void fromIdToken_should_return_auth_user_email() throws IOException {
    String idToken =
        createTokenWithPayload(
            parseValuesIntoJson(
                "https://idp.example", "sub-123", "email@example.com", "John", "Doe"));
    JsonNode payload = stubPayloadFromTokenJson(idToken);

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals("email@example.com", user.email());
    verify(authUserEmailMapper).resolveEmail(same(payload));
  }

  @Test
  void fromIdToken_should_return_auth_user_given_name() throws IOException {
    String idToken =
        createTokenWithPayload(
            parseValuesIntoJson(
                "https://idp.example", "sub-123", "email@example.com", "John", "Doe"));
    JsonNode payload = stubPayloadFromTokenJson(idToken);

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals(Optional.of("John"), user.givenName());
    verify(jsonPayloadClaims).text(same(payload), eq("given_name"));
  }

  @Test
  void fromIdToken_should_return_auth_user_family_name() throws IOException {
    String idToken =
        createTokenWithPayload(
            parseValuesIntoJson(
                "https://idp.example", "sub-123", "email@example.com", "John", "Doe"));
    JsonNode payload = stubPayloadFromTokenJson(idToken);

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals(Optional.of("Doe"), user.familyName());
    verify(jsonPayloadClaims).text(same(payload), eq("family_name"));
  }

  @Test
  void fromIdToken_should_throw_exception_when_failing_parsing_payload() throws Exception {
    String idToken = createTokenWithPayload("not-json");
    when(mapper.readTree(any(byte[].class)))
        .thenThrow(new JsonParseException(null, "Unexpected character"));

    assertThrows(IllegalArgumentException.class, () -> sut.fromIdToken(idToken));
  }

  private JsonNode stubPayloadFromTokenJson(String idToken) throws IOException {
    String payloadJson = new String(Base64.getUrlDecoder().decode(idToken.split("\\.")[1]));
    JsonNode payload = OBJECT_MAPPER.readTree(payloadJson);
    when(mapper.readTree(any(byte[].class))).thenReturn(payload);
    when(jsonPayloadClaims.text(same(payload), eq("iss"))).thenReturn("https://idp.example");
    when(jsonPayloadClaims.text(same(payload), eq("sub"))).thenReturn("sub-123");
    when(authUserEmailMapper.resolveEmail(same(payload))).thenReturn("email@example.com");
    when(jsonPayloadClaims.text(same(payload), eq("given_name"))).thenReturn("John");
    when(jsonPayloadClaims.text(same(payload), eq("family_name"))).thenReturn("Doe");
    return payload;
  }

  private static String parseValuesIntoJson(
      String issuer, String sub, String email, String givenName, String familyName) {
    StringBuilder json =
        new StringBuilder(
            "{\"iss\":\"%s\",\"sub\":\"%s\",\"email\":\"%s\"".formatted(issuer, sub, email));
    if (givenName != null) {
      json.append(",\"given_name\":\"%s\"".formatted(givenName));
    }
    if (familyName != null) {
      json.append(",\"family_name\":\"%s\"".formatted(familyName));
    }
    return json.append("}").toString();
  }

  private String createTokenWithPayload(String payloadJson) {
    String header =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
    String payload =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
    return header + "." + payload + ".signature";
  }
}
