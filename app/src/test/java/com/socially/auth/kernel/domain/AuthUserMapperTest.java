package com.socially.auth.kernel.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthUserMapperTest {

  @Mock private ObjectMapper mapper;

  @InjectMocks private AuthUserMapper sut;

  @ParameterizedTest
  @CsvSource(
      value = {"null", "''", "'   '"},
      nullValues = "null")
  void fromIdToken_should_return_empty_user_when_id_token_not_present(String idToken)
      throws IOException {
    AuthUser user = sut.fromIdToken(idToken);

    assertNull(user.id());
    assertNull(user.email());
    assertNull(user.name());
    verify(mapper, never()).readTree(any(byte[].class));
  }

  @Test
  void fromIdToken_should_throw_exception_when_second_token_part_is_not_present() {
    assertThrows(IllegalArgumentException.class, () -> sut.fromIdToken("header-only"));
  }

  @Test
  void fromIdToken_should_call_object_mapper_to_read_payload_tree() throws Exception {
    String payload = "{\"sub\":\"id-1\"}";
    String token = createTokenWithPayload(payload);

    when(mapper.readTree(any(byte[].class)))
        .thenAnswer(invocation -> new ObjectMapper().readTree((byte[]) invocation.getArgument(0)));

    sut.fromIdToken(token);

    verify(mapper).readTree(Base64.getUrlDecoder().decode(token.split("\\.")[1]));
  }

  @Test
  void fromIdToken_should_return_auth_user_id() throws IOException {
    String idToken =
        createTokenWithPayload(parseValuesIntoJson("sub-123", "email@example.com", "John", "Doe"));
    mockObjectMapperReadTree("sub-123", "email@example.com", "John", "Doe");

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals("sub-123", user.id());
  }

  @Test
  void fromIdToken_should_return_auth_user_email() throws IOException {
    String idToken =
        createTokenWithPayload(parseValuesIntoJson("sub-123", "email@example.com", "John", "Doe"));
    mockObjectMapperReadTree("sub-123", "email@example.com", "John", "Doe");

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals("email@example.com", user.email());
  }

  @ParameterizedTest
  @CsvSource(
      value = {"Jane,Brown,Jane Brown", "Jane,null,Jane", "null,Brown,Brown", "null,null,null"},
      nullValues = "null")
  void fromIdToken_should_return_auth_user_full_name(
      String givenName, String familyName, String expectedFullName) throws IOException {
    String idToken =
        createTokenWithPayload(
            parseValuesIntoJson("sub-456", "other@example.com", givenName, familyName));
    mockObjectMapperReadTree("sub-456", "other@example.com", givenName, familyName);

    AuthUser user = sut.fromIdToken(idToken);

    assertEquals(expectedFullName, user.name());
  }

  @Test
  void fromIdToken_should_throw_exception_when_failing_parsing_payload() {
    assertThrows(
        IllegalArgumentException.class, () -> sut.fromIdToken(createTokenWithPayload("not-json")));
  }

  private void mockObjectMapperReadTree(
      String sub, String email, String givenName, String familyName) throws IOException {
    when(mapper.readTree(any(byte[].class)))
        .thenReturn(
            new ObjectMapper().readTree(parseValuesIntoJson(sub, email, givenName, familyName)));
  }

  private static String parseValuesIntoJson(
      String sub, String email, String givenName, String familyName) {
    StringBuilder json =
        new StringBuilder("{\"sub\":\"%s\",\"email\":\"%s\"".formatted(sub, email));
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
