package com.socially.donor.kernel.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Id {
  private final UUID value;

  public static Id from(String uuid) {
    return new Id(UUID.fromString(uuid));
  }

  public static Id generate() {
    return new Id(UUID.randomUUID());
  }

  public UUID value() {
    return value;
  }
}
