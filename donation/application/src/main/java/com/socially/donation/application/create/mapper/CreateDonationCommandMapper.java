package com.socially.donation.application.create.mapper;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
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
