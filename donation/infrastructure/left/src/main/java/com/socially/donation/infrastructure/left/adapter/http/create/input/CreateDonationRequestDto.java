package com.socially.donation.infrastructure.left.adapter.http.create.input;

import jakarta.validation.constraints.NotNull;

public record CreateDonationRequestDto(
    @NotNull String id, @NotNull String title, @NotNull String description) {}
