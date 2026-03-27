package com.socially.donation.getbyid.application.input;

import jakarta.validation.constraints.NotNull;

public record FindDonationByIdQuery(@NotNull String id) {}
