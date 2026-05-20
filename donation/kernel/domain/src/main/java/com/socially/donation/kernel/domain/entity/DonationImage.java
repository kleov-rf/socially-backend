package com.socially.donation.kernel.domain.entity;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import java.time.Instant;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class DonationImage {

  public static final Long MAX_SIZE_BYTES = 5L * 1024 * 1024;

  private final Id id;
  private final StorageObjectKey storageObjectKey;
  private final ContentType contentType;
  private final Long sizeBytes;
  private final Boolean primary;
  private final Instant createdAt;

  public static DonationImage create(
      Id id,
      StorageObjectKey storageObjectKey,
      ContentType contentType,
      Long sizeBytes,
      Boolean primary,
      Instant createdAt) {
    validate(id, storageObjectKey, contentType, sizeBytes, primary, createdAt);
    return new DonationImage(id, storageObjectKey, contentType, sizeBytes, primary, createdAt);
  }

  private static void validate(
      Id id,
      StorageObjectKey storageObjectKey,
      ContentType contentType,
      Long sizeBytes,
      Boolean primary,
      Instant createdAt) {
    if (Objects.isNull(id)) {
      throw new IllegalArgumentException("donation image id cannot be null");
    }

    if (Objects.isNull(storageObjectKey)) {
      throw new IllegalArgumentException("donation image storage object key cannot be null");
    }

    if (Objects.isNull(contentType)) {
      throw new IllegalArgumentException("donation image content type cannot be null");
    }

    if (Objects.isNull(sizeBytes)) {
      throw new IllegalArgumentException("donation image size bytes cannot be null");
    }

    if (sizeBytes <= 0L) {
      throw new InvalidDonationImageException("Image size must be positive");
    }

    if (sizeBytes > MAX_SIZE_BYTES) {
      throw new InvalidDonationImageException(
          "Image size exceeds maximum of " + MAX_SIZE_BYTES + " bytes");
    }

    if (Objects.isNull(primary)) {
      throw new IllegalArgumentException("donation image primary flag cannot be null");
    }

    if (Objects.isNull(createdAt)) {
      throw new IllegalArgumentException("donation image created at cannot be null");
    }
  }

  public DonationImage withPrimary(Boolean primary) {
    return create(id, storageObjectKey, contentType, sizeBytes, primary, createdAt);
  }

  public Id id() {
    return id;
  }

  public StorageObjectKey storageObjectKey() {
    return storageObjectKey;
  }

  public ContentType contentType() {
    return contentType;
  }

  public Long sizeBytes() {
    return sizeBytes;
  }

  public Boolean primary() {
    return primary;
  }

  public Instant createdAt() {
    return createdAt;
  }
}
