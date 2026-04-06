package com.socially.donation.create.application.input.mapper;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public final class CreateDonationCommandMapper {
  public Donation toDomain(CreateDonationCommand command, Instant createdAt) {
    return Donation.create(
        Id.from(command.id()),
        Title.from(command.title()),
        Description.from(command.description()),
        createdAt);
  }
}
