package com.socially.donation.find.infrastructure.right.adapter.persistence;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class KeysetCursorCodec {
  private static final String DELIMITER = "|";
  private static final String PREVIOUS_PREFIX = "P:";

  public String encode(Instant createdAt, UUID id) {
    String payload = createdAt + DELIMITER + id;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  public String encodePrevious(Instant createdAt, UUID id) {
    String payload = PREVIOUS_PREFIX + createdAt + DELIMITER + id;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  public KeysetCursor decode(String encodedCursor) {
    try {
      String[] parts = decodePayloadParts(encodedCursor);

      String createdAtPart = parts[0];
      String idPart = parts[1];

      return new KeysetCursor(
          Instant.parse(stripPreviousPrefix(createdAtPart)), UUID.fromString(idPart));
    } catch (DateTimeParseException exception) {
      throw new IllegalArgumentException("Cursor is malformed", exception);
    }
  }

  public boolean isPreviousCursor(String encodedCursor) {
    String[] parts = decodePayloadParts(encodedCursor);
    return parts[0].startsWith(PREVIOUS_PREFIX);
  }

  private String @NonNull [] decodePayloadParts(String encodedCursor) {
    String payload =
        new String(Base64.getUrlDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
    String[] parts = payload.split("\\|", -1);
    if (parts.length != 2) {
      throw new IllegalArgumentException("Cursor is malformed");
    }
    return parts;
  }

  private String stripPreviousPrefix(String createdAtPart) {
    if (createdAtPart.startsWith(PREVIOUS_PREFIX)) {
      return createdAtPart.substring(PREVIOUS_PREFIX.length());
    }
    return createdAtPart;
  }
}
