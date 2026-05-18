package com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper;

import com.socially.donation.update.application.input.UpdateDonationCommand;
import com.socially.donation.update.application.input.UpdateDonationLocationCommand;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationLocationRequest;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import java.security.Principal;
import org.springframework.stereotype.Component;

@Component
public class UpdateDonationRequestMapper {

  public UpdateDonationCommand toCommand(
      String id, UpdateDonationRequest request, Principal principal) {
    UpdateDonationLocationRequest location = request.location();
    UpdateDonationLocationCommand locationCommand = null;
    if (location != null) {
      locationCommand =
          new UpdateDonationLocationCommand(
              location.address(), location.latitude(), location.longitude());
    }
    return new UpdateDonationCommand(
        id, request.title(), request.description(), locationCommand, principal);
  }
}
