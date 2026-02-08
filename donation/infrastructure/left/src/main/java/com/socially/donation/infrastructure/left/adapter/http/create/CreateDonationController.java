package com.socially.donation.infrastructure.left.adapter.http.create;

import com.socially.donation.application.create.CreateDonationCommandHandler;
import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.infrastructure.left.adapter.http.create.input.CreateDonationRequestDto;
import com.socially.donation.infrastructure.left.adapter.http.create.mapper.CreateDonationMapper;
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

  private final CreateDonationCommandHandler commandHandler;
  private final CreateDonationMapper mapper;

  @PostMapping
  public ResponseEntity<Void> create(@Valid @RequestBody CreateDonationRequestDto request) {
    CreateDonationCommand command = mapper.toCommand(request);
    commandHandler.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
