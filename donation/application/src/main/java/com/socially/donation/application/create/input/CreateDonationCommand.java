package com.socially.donation.application.create.input;

import jakarta.validation.constraints.NotNull;

public record CreateDonationCommand(
    @NotNull String id, @NotNull String title, @NotNull String description) {}
