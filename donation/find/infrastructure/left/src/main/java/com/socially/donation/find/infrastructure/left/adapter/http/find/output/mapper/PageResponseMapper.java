package com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.MetadataResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.PageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageResponseMapper {
  private final FindDonationResponseMapper donationResponseMapper;

  public PageResponse<FindDonationResponse> toResponse(Page<FindDonationDto> page) {
    List<FindDonationResponse> items =
        page.items().stream().map(donationResponseMapper::toResponse).toList();

    MetadataResponse metadata =
        new MetadataResponse(
            page.metadata().nextCursor(), page.metadata().hasNext(), page.metadata().size());

    return new PageResponse<>(items, metadata);
  }
}
