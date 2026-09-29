package com.socially.donation.update.infrastructure.left.adapter.http.update.input;

import jakarta.validation.Valid;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationRequest(
    Optional<String> title,
    Optional<String> description,
    Optional<@Valid UpdateDonationLocationRequest> location) {}
