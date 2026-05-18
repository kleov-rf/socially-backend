package com.socially.donation.update.infrastructure.left.adapter.http.update.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDonationLocationRequest(
    @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude) {}
