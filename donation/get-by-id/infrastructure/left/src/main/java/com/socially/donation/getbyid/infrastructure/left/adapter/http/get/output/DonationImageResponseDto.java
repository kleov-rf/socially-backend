package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output;

import jakarta.validation.constraints.NotNull;

public record DonationImageResponseDto(
    @NotNull String imageId,
    @NotNull String mediaUrl,
    @NotNull String contentType,
    @NotNull Long sizeBytes,
    @NotNull Boolean primary) {}
