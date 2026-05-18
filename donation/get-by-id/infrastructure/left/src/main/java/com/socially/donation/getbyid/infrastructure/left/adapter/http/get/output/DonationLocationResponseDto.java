package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output;

import jakarta.validation.constraints.NotNull;

public record DonationLocationResponseDto(
    @NotNull String address, @NotNull Double latitude, @NotNull Double longitude) {}
