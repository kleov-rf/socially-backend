package com.socially.donation.createimage.domain.model;

public record PresignedDonationImageUploadRequest(
    String storageObjectKey, String contentType, Long sizeBytes) {}
