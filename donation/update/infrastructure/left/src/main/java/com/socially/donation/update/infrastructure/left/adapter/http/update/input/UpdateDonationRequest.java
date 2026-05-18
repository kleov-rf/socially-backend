package com.socially.donation.update.infrastructure.left.adapter.http.update.input;

import jakarta.validation.Valid;

public record UpdateDonationRequest(
    String title, String description, @Valid UpdateDonationLocationRequest location) {}
