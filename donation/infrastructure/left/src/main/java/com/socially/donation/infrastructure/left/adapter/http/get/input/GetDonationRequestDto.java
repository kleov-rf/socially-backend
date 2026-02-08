package com.socially.donation.infrastructure.left.adapter.http.get.input;

import jakarta.validation.constraints.NotNull;

public record GetDonationRequestDto(
    @NotNull String id) {}
