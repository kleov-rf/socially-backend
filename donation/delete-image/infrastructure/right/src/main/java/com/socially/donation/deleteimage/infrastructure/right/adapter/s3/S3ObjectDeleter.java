package com.socially.donation.deleteimage.infrastructure.right.adapter.s3;

import org.springframework.stereotype.Component;

@Component
public final class S3ObjectDeleter {

  public void deleteObject(String bucket, String key) {
    throw new UnsupportedOperationException("Donation image storage delete not implemented yet");
  }
}
