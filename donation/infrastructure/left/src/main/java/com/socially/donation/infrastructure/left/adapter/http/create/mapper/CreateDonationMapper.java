package com.socially.donation.infrastructure.left.adapter.http.create.mapper;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.infrastructure.left.adapter.http.create.input.CreateDonationRequestDto;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationMapper {
  public CreateDonationCommand toCommand(CreateDonationRequestDto request) {
    return new CreateDonationCommand(request.id(), request.title(), request.description());
  }
}
