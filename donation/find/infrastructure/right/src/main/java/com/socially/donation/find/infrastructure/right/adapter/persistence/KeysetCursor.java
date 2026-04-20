package com.socially.donation.find.infrastructure.right.adapter.persistence;

import java.time.Instant;
import java.util.UUID;

public record KeysetCursor(Instant createdAt, UUID id) {}
