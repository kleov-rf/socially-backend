package com.socially.donation.infrastructure.left.adapter.http.update.input.mapper;

import com.socially.donation.application.update.input.UpdateDonationCommand;
import com.socially.donation.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import org.springframework.stereotype.Component;

@Component
public class UpdateDonationRequestMapper {

  public UpdateDonationCommand toCommand(String id, UpdateDonationRequest request) {
    return new UpdateDonationCommand(id, request.title(), request.description());
  }
}
