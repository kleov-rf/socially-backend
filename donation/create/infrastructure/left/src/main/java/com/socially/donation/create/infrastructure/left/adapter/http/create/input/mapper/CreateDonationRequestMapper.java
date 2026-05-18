package com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.CreateDonationLocationCommand;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationLocationRequest;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import java.security.Principal;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationRequestMapper {
  public CreateDonationCommand toCommand(CreateDonationRequest request, Principal principal) {
    CreateDonationLocationRequest location = request.location();
    return new CreateDonationCommand(
        request.id(),
        request.title(),
        request.description(),
        new CreateDonationLocationCommand(
            location.address(), location.latitude(), location.longitude()),
        principal);
  }
}
