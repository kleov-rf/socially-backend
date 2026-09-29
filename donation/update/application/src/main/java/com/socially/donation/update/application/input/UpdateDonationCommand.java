package com.socially.donation.update.application.input;

import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationCommand(
    @NotNull String id,
    Optional<String> title,
    Optional<String> description,
    Optional<UpdateDonationLocationCommand> location,
    @NotNull Principal principal) {}
