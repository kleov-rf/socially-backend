package com.socially.donation.kernel.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class DonorId {
  private final UUID value;

  public static DonorId from(String uuid) {
    return new DonorId(UUID.fromString(uuid));
  }

  public UUID value() {
    return value;
  }
}
