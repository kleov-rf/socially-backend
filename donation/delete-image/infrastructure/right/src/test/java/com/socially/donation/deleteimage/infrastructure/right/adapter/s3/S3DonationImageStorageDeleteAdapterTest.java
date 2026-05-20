package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.infrastructure.right.media.MediaStorageProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3DonationImageStorageDeleteAdapterTest {

  private static final String BUCKET = "socially-media";
  private static final String REGION = "us-east-1";
  private static final String OBJECT_KEY =
      "donations/550e8400-e29b-41d4-a716-446655440000/images/photo.jpg";
  private static final StorageObjectKey STORAGE_OBJECT_KEY = StorageObjectKey.from(OBJECT_KEY);

  @Mock private S3ObjectDeleter s3ObjectDeleter;
  @Mock private MediaStorageProperties mediaStorageProperties;

  @InjectMocks private S3DonationImageStorageDeleteAdapter adapter;

  private void stubValidS3Properties() {
    when(mediaStorageProperties.s3())
        .thenReturn(new MediaStorageProperties.S3(BUCKET, REGION, null, null, null));
  }

  @Test
  void deleteObject_should_call_deleter_with_bucket_and_key_from_storage_object_key() {
    stubValidS3Properties();

    adapter.deleteObject(STORAGE_OBJECT_KEY);

    verify(s3ObjectDeleter).deleteObject(BUCKET, OBJECT_KEY);
  }

  @Test
  void deleteObject_should_not_call_deleter_when_s3_bucket_not_configured() {
    when(mediaStorageProperties.s3())
        .thenReturn(new MediaStorageProperties.S3("", REGION, null, null, null));

    assertThrows(IllegalStateException.class, () -> adapter.deleteObject(STORAGE_OBJECT_KEY));

    verify(s3ObjectDeleter, never()).deleteObject(BUCKET, OBJECT_KEY);
  }
}
