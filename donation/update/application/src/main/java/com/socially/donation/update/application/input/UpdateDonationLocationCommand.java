package com.socially.donation.update.application.input;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record UpdateDonationLocationCommand(String address, Double latitude, Double longitude) {}
