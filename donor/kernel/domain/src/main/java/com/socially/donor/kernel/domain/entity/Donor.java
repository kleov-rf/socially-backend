package com.socially.donor.kernel.domain.entity;

import com.socially.donor.kernel.domain.valueobject.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Donor {

  @EqualsAndHashCode.Include private final Id id;
  private final UUID userId;
  private final String email;
  private final String givenName;
  private final String familyName;
  private final Instant createdAt;

  public static Donor create(
      Id id, UUID userId, String email, String givenName, String familyName, Instant createdAt) {
    return new Donor(id, userId, email, givenName, familyName, createdAt);
  }

  public Id id() {
    return id;
  }

  public UUID userId() {
    return userId;
  }

  public String email() {
    return email;
  }

  public String givenName() {
    return givenName;
  }

  public String familyName() {
    return familyName;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
