package com.socially.donation.infrastructure.left.adapter.http.create.mapper;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationRequestMapper {
  public CreateDonationCommand toCommand(CreateDonationRequest request) {
    return new CreateDonationCommand(request.id(), request.title(), request.description());
  }
}
