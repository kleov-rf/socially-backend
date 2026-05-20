package com.socially.donation.getbyid.application.output;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.List;

public record DonationDto(
    Id id,
    Title title,
    Description description,
    DonationLocationDto location,
    Instant createdAt,
    Instant lastUpdatedAt,
    DonorSummaryDto donor,
    List<DonationImageDto> images) {}
