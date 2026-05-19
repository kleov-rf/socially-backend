package com.socially.donation.createimage.application.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.security.Principal;

public record CreateDonationImageCommand(
    @NotNull String donationId,
    @NotBlank String originalFileName,
    @NotNull String contentType,
    @Positive long sizeBytes,
    boolean primary,
    @NotNull Principal principal) {}
