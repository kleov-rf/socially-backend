package com.socially.donation.getbyid.infrastructure.right.adapter.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CdnDonationImageMediaUrlAdapterTest {

  private static final String CDN_BASE_URL = "https://cdn.example.com";
  private static final String STORAGE_OBJECT_KEY = "donations/abc/images/key.jpg";
  private static final StorageObjectKey KEY = StorageObjectKey.from(STORAGE_OBJECT_KEY);
  private static final String EXPECTED_MEDIA_URL = CDN_BASE_URL + "/" + STORAGE_OBJECT_KEY;

  @Mock private MediaStorageProperties mediaStorageProperties;

  @InjectMocks private CdnDonationImageMediaUrlAdapter adapter;

  private static MediaStorageProperties.Cdn cdn(String baseUrl) {
    return new MediaStorageProperties.Cdn(baseUrl);
  }

  private void stubCdnBaseUrl(String cdnBaseUrl) {
    when(mediaStorageProperties.cdn()).thenReturn(cdn(cdnBaseUrl));
  }

  @Test
  void mediaUrlFor_should_read_cdn_from_media_storage_properties() {
    stubCdnBaseUrl(CDN_BASE_URL);

    adapter.mediaUrlFor(KEY);

    verify(mediaStorageProperties, atLeastOnce()).cdn();
    verifyNoMoreInteractions(mediaStorageProperties);
  }

  @Test
  void mediaUrlFor_should_return_cdn_base_plus_storage_object_key() {
    stubCdnBaseUrl(CDN_BASE_URL);

    String result = adapter.mediaUrlFor(KEY);

    assertEquals(EXPECTED_MEDIA_URL, result);
  }

  @Test
  void mediaUrlFor_should_throw_when_cdn_base_url_is_blank() {
    stubCdnBaseUrl("");

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> adapter.mediaUrlFor(KEY));

    assertEquals("media.storage.cdn.base-url must be configured", exception.getMessage());
  }

  @Test
  void mediaUrlFor_should_normalize_cdn_base_url_trailing_slash() {
    stubCdnBaseUrl(CDN_BASE_URL + "/");

    String result = adapter.mediaUrlFor(KEY);

    assertEquals(EXPECTED_MEDIA_URL, result);
  }
}
