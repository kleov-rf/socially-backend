package com.socially.donation.create.application.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDonationLocationCommand(
    @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude) {}
