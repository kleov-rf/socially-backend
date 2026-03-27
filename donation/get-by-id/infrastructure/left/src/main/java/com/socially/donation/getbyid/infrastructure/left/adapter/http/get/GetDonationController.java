package com.socially.donation.getbyid.infrastructure.left.adapter.http.get;

import com.socially.donation.getbyid.application.input.FindDonationByIdQuery;
import com.socially.donation.getbyid.application.port.left.FindDonationByIdUseCase;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper.DonationResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class GetDonationController {

  private final FindDonationByIdUseCase findDonationByIdUseCase;
  private final DonationResponseMapper mapper;

  @GetMapping("/{id}")
  public ResponseEntity<DonationResponseDto> get(@PathVariable String id) {
    FindDonationByIdQuery query = new FindDonationByIdQuery(id);
    return findDonationByIdUseCase
        .execute(query)
        .map(result -> ResponseEntity.ok(mapper.toResponse(result)))
        .orElse(ResponseEntity.notFound().build());
  }
}
