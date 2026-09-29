package com.socially.donation.find.infrastructure.left.adapter.http.find;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper.FindDonationsQueryMapper;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.PageResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.PageResponseMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogOperation("FIND_DONATIONS")
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class FindDonationsController {

  private final FindDonationsUseCase findDonationsUseCase;
  private final FindDonationsQueryMapper queryMapper;
  private final PageResponseMapper pageResponseMapper;

  @GetMapping
  public ResponseEntity<PageResponse<FindDonationResponse>> find(
      @RequestParam(required = false) Optional<String> cursor,
      @RequestParam(required = false) Optional<Integer> size,
      @RequestParam(required = false) Optional<String> order,
      @RequestParam(required = false) Optional<String> query,
      @RequestParam(required = false) Optional<Double> latitude,
      @RequestParam(required = false) Optional<Double> longitude) {
    FindDonationsQuery queryModel =
        queryMapper.toQuery(cursor, size, order, query, latitude, longitude);

    Page<FindDonationDto> page = findDonationsUseCase.execute(queryModel);

    PageResponse<FindDonationResponse> body = pageResponseMapper.toResponse(page);
    return ResponseEntity.ok(body);
  }
}
