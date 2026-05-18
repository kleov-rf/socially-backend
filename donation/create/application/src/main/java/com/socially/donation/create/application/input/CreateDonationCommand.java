package com.socially.donation.create.application.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;

public record CreateDonationCommand(
    @NotNull String id,
    @NotNull String title,
    @NotNull String description,
    @NotNull @Valid CreateDonationLocationCommand location,
    @NotNull Principal principal) {}
