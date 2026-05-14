package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OidcJsonPayloadClaimsTest {

  private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @InjectMocks private OidcJsonPayloadClaims sut;

  @Test
  void text_should_return_null_when_claim_is_missing() throws JsonProcessingException {
    JsonNode payload = OBJECT_MAPPER.readTree("{}");

    String actual = sut.text(payload, "email");

    assertNull(actual);
  }

  @Test
  void text_should_return_null_when_claim_is_json_null() throws JsonProcessingException {
    JsonNode payload = OBJECT_MAPPER.readTree("{\"email\":null}");

    String actual = sut.text(payload, "email");

    assertNull(actual);
  }

  @Test
  void text_should_return_null_when_value_is_blank() throws JsonProcessingException {
    JsonNode payload = OBJECT_MAPPER.readTree("{\"email\":\"   \"}");

    String actual = sut.text(payload, "email");

    assertNull(actual);
  }

  @Test
  void text_should_return_claim_text_when_present() throws JsonProcessingException {
    JsonNode payload = OBJECT_MAPPER.readTree("{\"email\":\"a@b.com\"}");

    String actual = sut.text(payload, "email");

    assertEquals("a@b.com", actual);
  }

  @Test
  void text_should_return_trimmed_claim_text_when_present() throws JsonProcessingException {
    JsonNode payload = OBJECT_MAPPER.readTree("{\"email\":\"   a@b.com   \"}");

    String actual = sut.text(payload, "email");

    assertEquals("a@b.com", actual);
  }
}
