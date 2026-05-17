package com.socially.donation.update.infrastructure.left.adapter.http.update;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.donation.update.application.port.left.UpdateDonationUseCase;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper.UpdateDonationRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogOperation("UPDATE_DONATION")
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class UpdateDonationController {

  private final UpdateDonationUseCase updateDonationUseCase;
  private final UpdateDonationRequestMapper updateDonationRequestMapper;

  @PatchMapping("/{id}")
  public ResponseEntity<Void> patch(
      @PathVariable String id, @RequestBody UpdateDonationRequest request) {
    var command = updateDonationRequestMapper.toCommand(id, request);
    updateDonationUseCase.execute(command);
    return ResponseEntity.noContent().build();
  }
}
