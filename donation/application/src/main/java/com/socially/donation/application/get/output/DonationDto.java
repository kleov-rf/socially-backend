package com.socially.donation.application.get.output;

import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;

public record DonationDto(Id id, Title title, Description description) {
}
