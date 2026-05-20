package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output;

public record CreateDonationImageResponse(
    String imageId,
    String uploadUrl,
    String mediaUrl,
    String contentType,
    Long sizeBytes,
    Boolean primary) {}
