package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class KeysetCursorCodecTest {

  private final KeysetCursorCodec codec = new KeysetCursorCodec();
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final UUID ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

  @Test
  void encode_should_call_encodeToString_with_delimited_received_createdAt_and_id() {
    String expectedPayload = CREATED_AT + "|" + ID;
    String expectedEncoded =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(expectedPayload.getBytes(StandardCharsets.UTF_8));

    String encoded = codec.encode(CREATED_AT, ID);

    assertEquals(expectedEncoded, encoded);
  }

  @Test
  void decode_should_throw_exception_if_received_encoded_doesnt_have_delimiter() {
    String encodedWithoutDelimiter =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString("2024-06-01T12:00:00Z".getBytes(StandardCharsets.UTF_8));

    assertThrows(IllegalArgumentException.class, () -> codec.decode(encodedWithoutDelimiter));
  }

  @Test
  void decode_should_throw_exception_if_received_encoded_has_more_than_one_delimiter() {
    String encodedWithMoreThanOneDelimiter =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                "2024-06-01T12:00:00Z|550e8400-e29b-41d4-a716-446655440000|extra"
                    .getBytes(StandardCharsets.UTF_8));

    assertThrows(
        IllegalArgumentException.class, () -> codec.decode(encodedWithMoreThanOneDelimiter));
  }

  @Test
  void decode_should_return_cursor_boundary_with_correct_values() {
    String encoded = codec.encode(CREATED_AT, ID);

    KeysetCursorCodec.CursorBoundary boundary = codec.decode(encoded);

    assertEquals(CREATED_AT, boundary.createdAt());
    assertEquals(ID, boundary.id());
  }

  @Test
  void decode_should_throw_exception_if_received_cursor_first_part_is_not_valid_instant() {
    String encodedWithInvalidInstant =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(("invalid-instant|" + ID).getBytes(StandardCharsets.UTF_8));

    assertThrows(IllegalArgumentException.class, () -> codec.decode(encodedWithInvalidInstant));
  }

  @Test
  void decode_should_return_same_encoded_values_round_trip() {
    String firstEncoded = codec.encode(CREATED_AT, ID);

    KeysetCursorCodec.CursorBoundary boundary = codec.decode(firstEncoded);
    String secondEncoded = codec.encode(boundary.createdAt(), boundary.id());

    assertEquals(firstEncoded, secondEncoded);
  }
}
