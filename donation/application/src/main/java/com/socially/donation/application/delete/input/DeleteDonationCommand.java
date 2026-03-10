package com.socially.donation.application.delete.input;

import jakarta.validation.constraints.NotNull;

public record DeleteDonationCommand(@NotNull String id) {}
