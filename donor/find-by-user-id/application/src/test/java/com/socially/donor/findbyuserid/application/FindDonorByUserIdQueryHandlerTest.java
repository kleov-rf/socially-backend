package com.socially.donor.findbyuserid.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.domain.port.right.FindDonorByUserIdRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonorByUserIdQueryHandlerTest {

  @Mock private FindDonorByUserIdRepository findDonorByUserIdRepository;

  @InjectMocks private FindDonorByUserIdQueryHandler handler;

  @Test
  void execute_should_call_repository_with_id_from_query() {
    var query = new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440010");
    when(findDonorByUserIdRepository.findByUserId(Id.from("550e8400-e29b-41d4-a716-446655440010")))
        .thenReturn(Optional.empty());

    handler.execute(query);

    verify(findDonorByUserIdRepository)
        .findByUserId(Id.from("550e8400-e29b-41d4-a716-446655440010"));
  }

  @Test
  void execute_should_return_result_from_repository_when_donor_exists() {
    var query = new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440010");
    Donor foundDonor =
        Donor.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001"),
            Id.from("550e8400-e29b-41d4-a716-446655440010"),
            "user@example.com",
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    when(findDonorByUserIdRepository.findByUserId(Id.from("550e8400-e29b-41d4-a716-446655440010")))
        .thenReturn(Optional.of(foundDonor));

    Optional<Donor> result = handler.execute(query);

    assertEquals(Optional.of(foundDonor), result);
  }

  @Test
  void execute_should_return_empty_when_repository_returns_empty() {
    var query = new FindDonorByUserIdQuery("550e8400-e29b-41d4-a716-446655440010");
    when(findDonorByUserIdRepository.findByUserId(Id.from("550e8400-e29b-41d4-a716-446655440010")))
        .thenReturn(Optional.empty());

    Optional<Donor> result = handler.execute(query);

    assertTrue(result.isEmpty());
  }
}
