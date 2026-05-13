package com.socially.donation.find.application.output;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;

public record FindDonationDto(
    Id id, Title title, Description description, Instant createdAt, Instant lastUpdatedAt) {}
