package com.socially.donation.getbyid.application.output;

public record DonationImageDto(
    String imageId, String mediaUrl, String contentType, Long sizeBytes, Boolean primary) {}
