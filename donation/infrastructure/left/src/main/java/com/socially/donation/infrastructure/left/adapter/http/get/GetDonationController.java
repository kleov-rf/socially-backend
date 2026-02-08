package com.socially.donation.infrastructure.left.adapter.http.get;

import com.socially.donation.application.get.FindDonationByIdQueryHandler;
import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.infrastructure.left.adapter.http.get.mapper.DonationResponseMapper;
import com.socially.donation.infrastructure.left.adapter.http.get.output.DonationResponseDto;
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

  private final FindDonationByIdQueryHandler queryHandler;
  private final DonationResponseMapper mapper;

  @GetMapping("/{id}")
  public ResponseEntity<DonationResponseDto> get(@PathVariable String id) {
    FindDonationByIdQuery query = new FindDonationByIdQuery(id);
    return queryHandler
        .handle(query)
        .map(result -> ResponseEntity.ok(mapper.toResponse(result)))
        .orElse(ResponseEntity.notFound().build());
  }
}
