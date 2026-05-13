package com.socially.donation.create.application.input.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public final class CreateDonationCommandMapper {
  public Donation toDomain(CreateDonationCommand command, String donorId, Instant now) {
    return Donation.create(
        Id.from(command.id()),
        DonorId.from(donorId),
        Title.from(command.title()),
        Description.from(command.description()),
        now,
        now);
  }
}
