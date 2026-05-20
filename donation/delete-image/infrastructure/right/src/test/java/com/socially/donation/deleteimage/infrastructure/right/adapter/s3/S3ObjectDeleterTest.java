package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3ObjectDeleterTest {

  private static final String BUCKET = "socially-media";
  private static final String KEY = "donations/abc/images/key.jpg";
  private static final String NOT_IMPLEMENTED_MESSAGE =
      "Donation image storage delete not implemented yet";

  @InjectMocks private S3ObjectDeleter deleter;

  @Test
  void deleteObject_should_throw_unsupported_operation_exception() {
    UnsupportedOperationException exception =
        assertThrows(UnsupportedOperationException.class, () -> deleter.deleteObject(BUCKET, KEY));

    assertEquals(NOT_IMPLEMENTED_MESSAGE, exception.getMessage());
  }
}
