package com.socially.user.kernel.domain.entity;

import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.commons.kernel.domain.valueobject.Id;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class User {

  @EqualsAndHashCode.Include private final Id id;
  private final Email email;
  private final String givenName;
  private final String familyName;
  private final Instant createdAt;

  public static User create(
      Id id, Email email, String givenName, String familyName, Instant createdAt) {
    return new User(id, email, givenName, familyName, createdAt);
  }

  public Id id() {
    return id;
  }

  public Email email() {
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
