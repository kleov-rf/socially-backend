package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateDonationImageRequest(
    @NotBlank String originalFileName,
    @NotNull String contentType,
    @Positive Long sizeBytes,
    @NotNull Boolean primary) {}
