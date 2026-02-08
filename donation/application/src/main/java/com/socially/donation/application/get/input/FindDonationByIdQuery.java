package com.socially.donation.application.get.input;

import jakarta.validation.constraints.NotNull;

public record FindDonationByIdQuery(@NotNull String id) {}
