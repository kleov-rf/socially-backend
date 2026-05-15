package com.socially.donation.find.infrastructure.left.adapter.http.find.output;

import jakarta.validation.constraints.NotNull;

public record FindDonorResponse(@NotNull String id, String givenName, String familyName) {}
