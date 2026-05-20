package com.socially.donation.deleteimage.application.input;

import jakarta.validation.constraints.NotNull;
import java.security.Principal;

public record DeleteDonationImageCommand(
    @NotNull String donationId, @NotNull String imageId, @NotNull Principal principal) {}
