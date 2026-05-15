package com.socially.donation.delete.infrastructure.left.adapter.http.delete;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.donation.delete.application.DonationForbiddenException;
import com.socially.donation.delete.application.DonationNotFoundException;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogOperation("DELETE_DONATION")
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DeleteDonationController {

  private final DeleteDonationUseCase deleteDonationUseCase;

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
    DeleteDonationCommand command = new DeleteDonationCommand(id, principal);
    try {
      deleteDonationUseCase.execute(command);
      return ResponseEntity.noContent().build();
    } catch (DonationNotFoundException exception) {
      return ResponseEntity.notFound().build();
    } catch (DonationForbiddenException exception) {
      return ResponseEntity.status(403).build();
    }
  }
}
