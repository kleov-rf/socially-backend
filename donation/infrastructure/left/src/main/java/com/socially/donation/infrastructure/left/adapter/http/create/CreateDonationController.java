package com.socially.donation.infrastructure.left.adapter.http.create;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.application.port.left.CreateDonationUseCase;
import com.socially.donation.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import com.socially.donation.infrastructure.left.adapter.http.create.mapper.CreateDonationRequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class CreateDonationController {

  private final CreateDonationUseCase createDonationUseCase;
  private final CreateDonationRequestMapper mapper;

  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateDonationRequest request) {
    CreateDonationCommand command = mapper.toCommand(request);
    createDonationUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
