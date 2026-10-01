package com.socially.user.kernel.domain.entity;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record User(
    Id id,
    Email email,
    Instant createdAt,
    Optional<String> givenName,
    Optional<String> familyName) {

  public static User create(Id id, Email email, Instant createdAt) {
    return new User(id, email, createdAt, Optional.empty(), Optional.empty());
  }

  public User withGivenName(Optional<String> givenName) {
    return new User(id, email, createdAt, givenName, familyName);
  }

  public User withFamilyName(Optional<String> familyName) {
    return new User(id, email, createdAt, givenName, familyName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
