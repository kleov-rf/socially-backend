package com.socially.donation.kernel.infrastructure.right.media;

import org.springframework.util.StringUtils;

public final class MediaUrlBuilder {

  private MediaUrlBuilder() {}

  public static String buildMediaUrl(String cdnBaseUrl, String storageObjectKey) {
    String normalizedBaseUrl =
        cdnBaseUrl.endsWith("/") ? cdnBaseUrl.substring(0, cdnBaseUrl.length() - 1) : cdnBaseUrl;
    String normalizedKey =
        storageObjectKey.startsWith("/") ? storageObjectKey.substring(1) : storageObjectKey;
    return normalizedBaseUrl + "/" + normalizedKey;
  }

  public static void validateCdnBaseUrl(String cdnBaseUrl) {
    if (!StringUtils.hasText(cdnBaseUrl)) {
      throw new IllegalStateException("media.storage.cdn.base-url must be configured");
    }
  }
}
