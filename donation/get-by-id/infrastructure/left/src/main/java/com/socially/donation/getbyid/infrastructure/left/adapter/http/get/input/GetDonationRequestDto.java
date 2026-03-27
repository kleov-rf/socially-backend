package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.input;

import jakarta.validation.constraints.NotNull;

public record GetDonationRequestDto(@NotNull String id) {}
