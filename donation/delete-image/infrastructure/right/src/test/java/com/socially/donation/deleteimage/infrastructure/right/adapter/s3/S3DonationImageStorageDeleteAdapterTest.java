package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3DonationImageStorageDeleteAdapterTest {

  private static final StorageObjectKey STORAGE_OBJECT_KEY =
      StorageObjectKey.from("donations/550e8400-e29b-41d4-a716-446655440000/images/photo.jpg");
  private static final String NOT_IMPLEMENTED_MESSAGE =
      "Donation image storage delete not implemented yet";

  @InjectMocks private S3DonationImageStorageDeleteAdapter adapter;

  @Test
  void deleteObject_should_throw_unsupported_operation_exception() {
    UnsupportedOperationException exception =
        assertThrows(
            UnsupportedOperationException.class, () -> adapter.deleteObject(STORAGE_OBJECT_KEY));

    assertEquals(NOT_IMPLEMENTED_MESSAGE, exception.getMessage());
  }
}
