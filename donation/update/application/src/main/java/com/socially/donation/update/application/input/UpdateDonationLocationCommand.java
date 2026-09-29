package com.socially.donation.update.application.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationLocationCommand(
    @NotBlank String address, @NotNull Double latitude, @NotNull Double longitude) {}
