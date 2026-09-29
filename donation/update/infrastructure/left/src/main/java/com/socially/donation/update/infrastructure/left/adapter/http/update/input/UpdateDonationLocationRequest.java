package com.socially.donation.update.infrastructure.left.adapter.http.update.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationLocationRequest(
    @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude) {}
