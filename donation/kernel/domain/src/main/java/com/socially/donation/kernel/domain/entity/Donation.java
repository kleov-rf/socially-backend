package com.socially.donation.kernel.domain.entity;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Donation {

  @EqualsAndHashCode.Include private final Id id;
  private final Id donorId;
  private final Title title;
  private final Description description;
  private final Instant createdAt;
  private final Instant lastUpdatedAt;

  public static Donation create(
      Id id,
      Id donorId,
      Title title,
      Description description,
      Instant createdAt,
      Instant lastUpdatedAt) {
    return new Donation(id, donorId, title, description, createdAt, lastUpdatedAt);
  }

  public Donation withTitle(Title title, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, createdAt, lastUpdatedAt);
  }

  public Donation withDescription(Description description, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, createdAt, lastUpdatedAt);
  }

  public Id id() {
    return id;
  }

  public Title title() {
    return title;
  }

  public Id donorId() {
    return donorId;
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
}
