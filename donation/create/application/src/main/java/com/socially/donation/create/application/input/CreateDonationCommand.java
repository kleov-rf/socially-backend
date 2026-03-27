package com.socially.donation.create.application.input;

import jakarta.validation.constraints.NotNull;

public record CreateDonationCommand(
    @NotNull String id, @NotNull String title, @NotNull String description) {}
