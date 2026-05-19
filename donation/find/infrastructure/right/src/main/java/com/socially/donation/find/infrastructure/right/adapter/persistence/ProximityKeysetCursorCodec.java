package com.socially.donation.find.infrastructure.right.adapter.persistence;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProximityKeysetCursorCodec {
  private static final String DELIMITER = "|";
  private static final String PREVIOUS_PREFIX = "P:";

  public String encode(double distanceMeters, UUID id) {
    String payload = distanceMeters + DELIMITER + id;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  public String encodePrevious(double distanceMeters, UUID id) {
    String payload = PREVIOUS_PREFIX + distanceMeters + DELIMITER + id;
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
  }

  public ProximityKeysetCursor decode(String encodedCursor) {
    String[] parts = decodePayloadParts(encodedCursor);

    String distancePart = parts[0];
    String idPart = parts[1];

    return new ProximityKeysetCursor(
        Double.parseDouble(stripPreviousPrefix(distancePart)), UUID.fromString(idPart));
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

  private String stripPreviousPrefix(String distancePart) {
    if (distancePart.startsWith(PREVIOUS_PREFIX)) {
      return distancePart.substring(PREVIOUS_PREFIX.length());
    }
    return distancePart;
  }
}
