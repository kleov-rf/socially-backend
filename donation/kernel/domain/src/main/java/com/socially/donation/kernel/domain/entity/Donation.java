package com.socially.donation.kernel.domain.entity;

import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Donation {

  private final Id id;
  private final Title title;
  private final Description description;
  private final Instant createdAt;
  private final Instant lastUpdatedAt;

  public static Donation create(
      Id id, Title title, Description description, Instant createdAt, Instant lastUpdatedAt) {
    return new Donation(id, title, description, createdAt, lastUpdatedAt);
  }

  public Donation withTitle(Title title) {
    return create(id, title, description, createdAt, lastUpdatedAt);
  }

  public Donation withDescription(Description description) {
    return create(id, title, description, createdAt, lastUpdatedAt);
  }

  public Id id() {
    return id;
  }

  public Title title() {
    return title;
  }

  public Description description() {
    return description;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant lastUpdatedAt() {
    return lastUpdatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Donation donation = (Donation) o;
    return Objects.equals(id, donation.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
