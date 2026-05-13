package com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import java.security.Principal;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationRequestMapper {
  public CreateDonationCommand toCommand(CreateDonationRequest request, Principal principal) {
    return new CreateDonationCommand(
        request.id(), request.title(), request.description(), principal);
  }
}
