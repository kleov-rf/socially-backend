package com.socially.donor.create.application.input;

import jakarta.validation.constraints.NotNull;

public record CreateDonorCommand(
    @NotNull String id,
    @NotNull String userId,
    @NotNull String email,
    String givenName,
    String familyName) {}
