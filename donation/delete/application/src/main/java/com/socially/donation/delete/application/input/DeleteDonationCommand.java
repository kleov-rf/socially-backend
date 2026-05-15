package com.socially.donation.delete.application.input;

import jakarta.validation.constraints.NotNull;
import java.security.Principal;

public record DeleteDonationCommand(@NotNull String id, @NotNull Principal principal) {}
