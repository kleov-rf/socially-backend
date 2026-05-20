package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record DonationResponseDto(
    @NotNull String id,
    @NotNull String title,
    @NotNull String description,
    @NotNull DonationLocationResponseDto location,
    @NotNull Instant createdAt,
    @NotNull Instant lastUpdatedAt,
    @NotNull DonorResponseDto donor,
    @NotNull List<DonationImageResponseDto> images) {}
