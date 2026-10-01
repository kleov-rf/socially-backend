package com.socially.donor.kernel.domain.entity;

import com.socially.commons.kernel.domain.valueobject.Id;
import java.time.Instant;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Donor(
    Id id,
    Id userId,
    String email,
    Instant createdAt,
    Optional<String> givenName,
    Optional<String> familyName) {

  public static Donor create(Id id, Id userId, String email, Instant createdAt) {
    return new Donor(id, userId, email, createdAt, Optional.empty(), Optional.empty());
  }

  public Donor withGivenName(Optional<String> givenName) {
    return new Donor(id, userId, email, createdAt, givenName, familyName);
  }

  public Donor withFamilyName(Optional<String> familyName) {
    return new Donor(id, userId, email, createdAt, givenName, familyName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Donor other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
