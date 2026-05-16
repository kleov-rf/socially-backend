package com.socially.donation.create.application.input.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public final class CreateDonationCommandMapper {
  public Donation toDomain(CreateDonationCommand command, Donor donor, Instant now) {
    return Donation.create(
        Id.from(command.id()),
        Id.from(donor.id().value().toString()),
        Title.from(command.title()),
        Description.from(command.description()),
        now,
        now);
  }
}
