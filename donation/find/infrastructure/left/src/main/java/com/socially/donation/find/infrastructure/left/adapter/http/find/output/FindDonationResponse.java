package com.socially.donation.find.infrastructure.left.adapter.http.find.output;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record FindDonationResponse(
    @NotNull String id,
    @NotNull String title,
    @NotNull String description,
    @NotNull Instant createdAt,
    @NotNull Instant lastUpdatedAt) {}
