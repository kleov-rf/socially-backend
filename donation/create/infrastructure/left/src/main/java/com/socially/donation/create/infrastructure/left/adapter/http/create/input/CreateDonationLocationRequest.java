package com.socially.donation.create.infrastructure.left.adapter.http.create.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDonationLocationRequest(
    @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude) {}
