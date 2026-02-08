package com.socially.donation.application.create.mapper;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CreateDonationMapper {
  public static Donation toDonation(CreateDonationCommand command) {
    return Donation.create(
        Id.from(command.id()),
        Title.from(command.title()),
        Description.from(command.description()));
  }
}
