package com.socially.donation.kernel.domain.entity;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.exception.DonationImageNotFoundException;
import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Donation {

  public static final Integer MAX_IMAGES = 5;

  @EqualsAndHashCode.Include private final Id id;
  private final Id donorId;
  private final Title title;
  private final Description description;
  private final DonationLocation location;
  private final Instant createdAt;
  private final Instant lastUpdatedAt;
  private final List<DonationImage> images;

  public static Donation create(
      Id id,
      Id donorId,
      Title title,
      Description description,
      DonationLocation location,
      Instant createdAt,
      Instant lastUpdatedAt) {
    return create(id, donorId, title, description, location, createdAt, lastUpdatedAt, List.of());
  }

  public static Donation create(
      Id id,
      Id donorId,
      Title title,
      Description description,
      DonationLocation location,
      Instant createdAt,
      Instant lastUpdatedAt,
      List<DonationImage> images) {
    validate(id, donorId, title, description, location, createdAt, lastUpdatedAt, images);
    return new Donation(
        id, donorId, title, description, location, createdAt, lastUpdatedAt, List.copyOf(images));
  }

  private static void validate(
      Id id,
      Id donorId,
      Title title,
      Description description,
      DonationLocation location,
      Instant createdAt,
      Instant lastUpdatedAt,
      List<DonationImage> images) {
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

    if (images == null) {
      throw new IllegalArgumentException("donation images cannot be null");
    }
  }

  public Donation withTitle(Title title, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, location, createdAt, lastUpdatedAt, images);
  }

  public Donation withDescription(Description description, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, location, createdAt, lastUpdatedAt, images);
  }

  public Donation withLocation(DonationLocation location, Instant lastUpdatedAt) {
    return create(id, donorId, title, description, location, createdAt, lastUpdatedAt, images);
  }

  public Donation withImageAdded(DonationImage image) {
    if (Objects.isNull(image)) {
      throw new IllegalArgumentException("donation image cannot be null");
    }

    if (images.size() >= MAX_IMAGES) {
      throw new InvalidDonationImageException(
          "Donation " + id.value() + " cannot have more than " + MAX_IMAGES + " images");
    }

    List<DonationImage> updatedImages = new ArrayList<>(images.size() + 1);
    for (DonationImage existing : images) {
      if (Boolean.TRUE.equals(image.primary()) && Boolean.TRUE.equals(existing.primary())) {
        updatedImages.add(existing.withPrimary(Boolean.FALSE));
      } else {
        updatedImages.add(existing);
      }
    }
    updatedImages.add(image);

    return create(
        id, donorId, title, description, location, createdAt, lastUpdatedAt, updatedImages);
  }

  public Donation withImageRemoved(Id imageId) {
    if (Objects.isNull(imageId)) {
      throw new IllegalArgumentException("donation image id cannot be null");
    }

    DonationImage removedImage = null;
    List<DonationImage> updatedImages = new ArrayList<>(images.size());
    for (DonationImage existing : images) {
      if (existing.id().equals(imageId)) {
        removedImage = existing;
      } else {
        updatedImages.add(existing);
      }
    }

    if (Objects.isNull(removedImage)) {
      throw new DonationImageNotFoundException(id, imageId);
    }

    if (Boolean.TRUE.equals(removedImage.primary()) && !updatedImages.isEmpty()) {
      boolean hasPrimary =
          updatedImages.stream().anyMatch(image -> Boolean.TRUE.equals(image.primary()));
      if (!hasPrimary) {
        updatedImages.set(0, updatedImages.getFirst().withPrimary(Boolean.TRUE));
      }
    }

    return create(
        id, donorId, title, description, location, createdAt, lastUpdatedAt, updatedImages);
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

  public Boolean belongsToDonor(Id donorId) {
    return Boolean.valueOf(this.donorId.equals(donorId));
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

  public List<DonationImage> images() {
    return images;
  }
}
