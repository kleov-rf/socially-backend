package com.socially.donation.find.infrastructure.right.adapter.persistence;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class KeysetCursorCodec {
  private static final String DELIMITER = "|";

  public String encode(Instant createdAt, UUID id) {
    String payload = createdAt + DELIMITER + id;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  public CursorBoundary decode(String encodedCursor) {
    try {
      String payload =
          new String(Base64.getUrlDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
      String[] parts = payload.split("\\|", -1);
      if (parts.length != 2) {
        throw new IllegalArgumentException("Cursor is malformed");
      }

      return new CursorBoundary(Instant.parse(parts[0]), UUID.fromString(parts[1]));
    } catch (DateTimeParseException exception) {
      throw new IllegalArgumentException("Cursor is malformed", exception);
    }
  }

  public record CursorBoundary(Instant createdAt, UUID id) {}
}
