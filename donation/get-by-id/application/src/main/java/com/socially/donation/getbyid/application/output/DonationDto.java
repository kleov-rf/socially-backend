package com.socially.donation.getbyid.application.output;

import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;

public record DonationDto(Id id, Title title, Description description) {}
