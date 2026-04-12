package com.socially.donation.find.infrastructure.left.adapter.http.find;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.FindDonationResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class FindDonationsController {

  private final FindDonationsUseCase findDonationsUseCase;
  private final FindDonationResponseMapper mapper;

  @GetMapping
  public ResponseEntity<List<FindDonationResponse>> find() {
    List<FindDonationDto> donations = findDonationsUseCase.execute(new FindDonationsQuery());
    List<FindDonationResponse> body = donations.stream().map(mapper::toResponse).toList();
    return ResponseEntity.ok(body);
  }
}
