package com.socially.donation.deleteimage.infrastructure.left.adapter.http.deleteimage;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.donation.deleteimage.application.input.DeleteDonationImageCommand;
import com.socially.donation.deleteimage.application.port.left.DeleteDonationImageUseCase;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogOperation("DELETE_DONATION_IMAGE")
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DeleteDonationImageController {

  private final DeleteDonationImageUseCase deleteDonationImageUseCase;

  @DeleteMapping("/{donationId}/images/{imageId}")
  public ResponseEntity<Void> delete(
      @PathVariable String donationId, @PathVariable String imageId, Principal principal) {
    DeleteDonationImageCommand command =
        new DeleteDonationImageCommand(donationId, imageId, principal);
    deleteDonationImageUseCase.execute(command);
    return ResponseEntity.noContent().build();
  }
}
