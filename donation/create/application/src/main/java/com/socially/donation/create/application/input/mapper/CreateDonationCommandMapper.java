package com.socially.donation.create.application.input.mapper;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import org.springframework.stereotype.Component;

@Component
public final class CreateDonationCommandMapper {
  public Donation toDomain(CreateDonationCommand command) {
    return Donation.create(
        Id.from(command.id()),
        Title.from(command.title()),
        Description.from(command.description()));
  }
}
