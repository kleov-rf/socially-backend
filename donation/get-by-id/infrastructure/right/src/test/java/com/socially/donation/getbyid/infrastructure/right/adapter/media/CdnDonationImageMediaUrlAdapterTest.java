package com.socially.donation.getbyid.infrastructure.right.adapter.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CdnDonationImageMediaUrlAdapterTest {

  private static final StorageObjectKey KEY = StorageObjectKey.from("donations/abc/images/key.jpg");

  @InjectMocks private CdnDonationImageMediaUrlAdapter adapter;

  @Test
  void mediaUrlFor_should_throw_unsupported_operation_exception() {
    UnsupportedOperationException exception =
        assertThrows(UnsupportedOperationException.class, () -> adapter.mediaUrlFor(KEY));

    assertEquals("Donation image media URL resolution is not implemented.", exception.getMessage());
  }
}
