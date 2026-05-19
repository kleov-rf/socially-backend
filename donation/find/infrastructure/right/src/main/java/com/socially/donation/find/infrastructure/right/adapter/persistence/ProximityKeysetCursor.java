package com.socially.donation.find.infrastructure.right.adapter.persistence;

import java.util.UUID;

public record ProximityKeysetCursor(double distanceMeters, UUID id) {}
