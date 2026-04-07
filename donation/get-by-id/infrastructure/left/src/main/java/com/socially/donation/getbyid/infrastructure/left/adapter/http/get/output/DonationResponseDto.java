package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record DonationResponseDto(
    @NotNull String id,
    @NotNull String title,
    @NotNull String description,
    @NotNull Instant createdAt,
    @NotNull Instant lastUpdatedAt) {}
