package com.socially.donation.find.infrastructure.left.adapter.http.find;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper.FindDonationsQueryMapper;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.PageResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.PageResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class FindDonationsController {

  private final FindDonationsUseCase findDonationsUseCase;
  private final FindDonationsQueryMapper queryMapper;
  private final PageResponseMapper pageResponseMapper;

  @GetMapping
  public ResponseEntity<PageResponse<FindDonationResponse>> find(
      @RequestParam(required = false) String cursor, @RequestParam(required = false) Integer size) {
    FindDonationsQuery query = queryMapper.toQuery(cursor, size);

    Page<FindDonationDto> page = findDonationsUseCase.execute(query);

    PageResponse<FindDonationResponse> body = pageResponseMapper.toResponse(page);
    return ResponseEntity.ok(body);
  }
}
