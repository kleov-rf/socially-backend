package com.socially.donation.createimage.application.output;

public record CreateDonationImageResult(
    String imageId,
    String uploadUrl,
    String mediaUrl,
    String contentType,
    Long sizeBytes,
    Boolean primary) {}
