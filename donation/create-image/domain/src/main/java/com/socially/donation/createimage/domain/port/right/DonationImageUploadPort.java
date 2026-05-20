package com.socially.donation.createimage.domain.port.right;

import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;

public interface DonationImageUploadPort {
  PresignedDonationImageUpload issueUpload(PresignedDonationImageUploadRequest request);
}
