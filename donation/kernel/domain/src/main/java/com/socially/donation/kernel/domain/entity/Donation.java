package com.socially.donation.kernel.domain.entity;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
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
  private final DonationLocation location;
  private final Instant createdAt;
  private final Instant lastUpdatedAt;

  public static Donation create(
      Id id,
      Id donorId,
      Title title,
      Description description,
      DonationLocation location,
      Instant createdAt,
      Instant lastUpdatedAt) {
    validate(id, donorId, title, description, location, createdAt, lastUpdatedAt);
    return new Donation(id, donorId, title, description, location, createdAt, lastUpdatedAt);
  }

  private static void validate(
      Id id,
      Id donorId,
      Title title,
      Description description,
      DonationLocation location,
      Instant createdAt,
      Instant lastUpdatedAt) {
    if (id == null) {
      throw new IllegalArgumentException("donation id cannot be null");
    }

    if (donorId == null) {
      throw new IllegalArgumentException("donation donor id cannot be null");
    }

    if (title == null) {
      throw new IllegalArgumentException("donation title cannot be null");
    }

    if (description == null) {
      throw new IllegalArgumentException("donation description cannot be null");
    }

    if (location == null) {
      throw new IllegalArgumentException("donation location cannot be null");
    }

    if (createdAt == null) {
      throw new IllegalArgumentException("donation created at cannot be null");
    }

    if (lastUpdatedAt == null) {
      throw new IllegalArgumentException("donation last updated at cannot be null");
    }
  }

  public Donation withTitle(Title title, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, location, createdAt, lastUpdatedAt);
  }

  public Donation withDescription(Description description, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, location, createdAt, lastUpdatedAt);
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

  public boolean belongsToDonor(Id donorId) {
    return this.donorId.equals(donorId);
  }

  public Description description() {
    return description;
  }

  public DonationLocation location() {
    return location;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant lastUpdatedAt() {
    return lastUpdatedAt;
  }
}
