package com.socially.donation.update.application.input;

import jakarta.validation.constraints.NotNull;
import java.security.Principal;

public record UpdateDonationCommand(
    @NotNull String id, String title, String description, @NotNull Principal principal) {}
