package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.application.port.left.CreateDonationImageUseCase;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.CreateDonationImageRequest;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.mapper.CreateDonationImageRequestMapper;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.CreateDonationImageResponse;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.mapper.CreateDonationImageResponseMapper;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogOperation("CREATE_DONATION_IMAGE")
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class CreateDonationImageController {

  private final CreateDonationImageUseCase createDonationImageUseCase;
  private final CreateDonationImageRequestMapper requestMapper;
  private final CreateDonationImageResponseMapper responseMapper;

  @PostMapping("/{donationId}/images")
  public ResponseEntity<CreateDonationImageResponse> create(
      @PathVariable String donationId,
      @Valid @RequestBody CreateDonationImageRequest request,
      Principal principal) {
    CreateDonationImageCommand command = requestMapper.toCommand(donationId, request, principal);
    var result = createDonationImageUseCase.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(result));
  }
}
