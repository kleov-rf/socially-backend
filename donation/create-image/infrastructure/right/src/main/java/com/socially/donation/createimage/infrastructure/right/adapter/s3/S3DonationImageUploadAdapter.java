package com.socially.donation.createimage.infrastructure.right.adapter.s3;

import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;
import com.socially.donation.createimage.domain.port.right.DonationImageUploadPort;
import org.springframework.stereotype.Service;

@Service
public final class S3DonationImageUploadAdapter implements DonationImageUploadPort {

  @Override
  public PresignedDonationImageUpload issueUpload(PresignedDonationImageUploadRequest request) {
    throw new UnsupportedOperationException("S3 donation image upload not implemented yet");
  }
}
