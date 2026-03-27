package com.socially.donation.create.infrastructure.left.adapter.http.create.input;

import jakarta.validation.constraints.NotNull;

public record CreateDonationRequest(
    @NotNull String id, @NotNull String title, @NotNull String description) {}
