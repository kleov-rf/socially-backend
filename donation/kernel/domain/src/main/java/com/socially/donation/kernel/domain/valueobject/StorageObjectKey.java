package com.socially.donation.kernel.domain.valueobject;

import com.socially.commons.kernel.domain.valueobject.Id;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@EqualsAndHashCode
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class StorageObjectKey {

  private final String value;

  public static StorageObjectKey from(String value) {
    if (Objects.isNull(value)) {
      throw new IllegalArgumentException("storage object key cannot be null");
    }

    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException("storage object key cannot be blank");
    }

    return new StorageObjectKey(value.trim());
  }

  public static StorageObjectKey forDonationImage(
      Id donationId, Id imageId, String originalFileName) {
    if (Objects.isNull(donationId)) {
      throw new IllegalArgumentException("donation id cannot be null");
    }

    if (Objects.isNull(imageId)) {
      throw new IllegalArgumentException("image id cannot be null");
    }

    if (Objects.isNull(originalFileName)) {
      throw new IllegalArgumentException("original file name cannot be null");
    }

    String extension = extractExtension(originalFileName);
    String key =
        "donations/%s/images/%s%s".formatted(donationId.value(), imageId.value(), extension);
    return from(key);
  }

  private static String extractExtension(String originalFileName) {
    Integer dotIndex = originalFileName.lastIndexOf('.');
    if (dotIndex < 0 || dotIndex.equals(originalFileName.length() - 1)) {
      return "";
    }
    return originalFileName.substring(dotIndex);
  }

  public String value() {
    return value;
  }
}
