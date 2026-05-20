package com.socially.donation.createimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MediaUrlBuilderTest {

  private static final String CDN_BASE_URL = "https://cdn.example.com";
  private static final String STORAGE_OBJECT_KEY = "donations/abc/images/key.jpg";
  private static final String EXPECTED_MEDIA_URL = CDN_BASE_URL + "/" + STORAGE_OBJECT_KEY;

  @Test
  void buildMediaUrl_should_join_base_and_key_when_no_extra_slashes() {
    String actual = MediaUrlBuilder.buildMediaUrl(CDN_BASE_URL, STORAGE_OBJECT_KEY);

    assertEquals(EXPECTED_MEDIA_URL, actual);
  }

  @Test
  void buildMediaUrl_should_strip_trailing_slash_from_base() {
    String actual = MediaUrlBuilder.buildMediaUrl(CDN_BASE_URL + "/", STORAGE_OBJECT_KEY);

    assertEquals(EXPECTED_MEDIA_URL, actual);
  }

  @Test
  void buildMediaUrl_should_strip_leading_slash_from_key() {
    String actual = MediaUrlBuilder.buildMediaUrl(CDN_BASE_URL, "/" + STORAGE_OBJECT_KEY);

    assertEquals(EXPECTED_MEDIA_URL, actual);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void validateCdnBaseUrl_should_throw_when_base_url_is_blank(String cdnBaseUrl) {
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> MediaUrlBuilder.validateCdnBaseUrl(cdnBaseUrl));

    assertEquals("media.storage.cdn.base-url must be configured", exception.getMessage());
  }
}
