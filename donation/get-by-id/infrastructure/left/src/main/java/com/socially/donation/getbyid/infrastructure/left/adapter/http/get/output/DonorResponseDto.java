package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output;

import jakarta.validation.constraints.NotNull;

public record DonorResponseDto(
    @NotNull String id, @NotNull String email, String givenName, String familyName) {}
