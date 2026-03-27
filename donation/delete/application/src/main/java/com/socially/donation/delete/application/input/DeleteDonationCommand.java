package com.socially.donation.delete.application.input;

import jakarta.validation.constraints.NotNull;

public record DeleteDonationCommand(@NotNull String id) {}
