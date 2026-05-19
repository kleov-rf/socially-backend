package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.socially.commons.kernel.domain.valueobject.Id;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProximityKeysetCursorCodecTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final double DISTANCE_METERS = 1234.5;
  private static final UUID ID = Id.from(DONATION_ID).value();

  private final ProximityKeysetCursorCodec sut = new ProximityKeysetCursorCodec();

  @Test
  void encode_should_produce_decodable_cursor() {
    String encoded = sut.encode(DISTANCE_METERS, ID);

    assertFalse(encoded.isBlank());
  }

  @Test
  void decode_should_restore_distance_meters() {
    String encoded = sut.encode(DISTANCE_METERS, ID);

    ProximityKeysetCursor decoded = sut.decode(encoded);

    assertEquals(DISTANCE_METERS, decoded.distanceMeters());
  }

  @Test
  void decode_should_restore_id() {
    String encoded = sut.encode(DISTANCE_METERS, ID);

    ProximityKeysetCursor decoded = sut.decode(encoded);

    assertEquals(ID, decoded.id());
  }

  @Test
  void encodePrevious_should_mark_cursor_as_previous() {
    String encoded = sut.encodePrevious(987.0, ID);

    assertTrue(sut.isPreviousCursor(encoded));
  }

  @Test
  void encodePrevious_should_restore_distance_meters() {
    String encoded = sut.encodePrevious(987.0, ID);

    ProximityKeysetCursor decoded = sut.decode(encoded);

    assertEquals(987.0, decoded.distanceMeters());
  }

  @Test
  void isPreviousCursor_should_return_false_for_forward_cursor() {
    String encoded = sut.encode(DISTANCE_METERS, ID);

    assertFalse(sut.isPreviousCursor(encoded));
  }

  @Test
  void decode_should_throw_when_cursor_is_malformed() {
    assertThrows(IllegalArgumentException.class, () -> sut.decode("not-a-valid-cursor"));
  }
}
