package com.socially.donor.findbyid.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyid.application.input.FindDonorByIdQuery;
import com.socially.donor.findbyid.domain.port.right.FindDonorByIdRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonorByIdQueryHandlerTest {

  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";

  @Mock private FindDonorByIdRepository findDonorByIdRepository;

  @InjectMocks private FindDonorByIdQueryHandler handler;

  @Test
  void execute_should_call_repository_with_donor_id_from_query() {
    var query = new FindDonorByIdQuery(DONOR_ID);
    when(findDonorByIdRepository.findById(Id.from(DONOR_ID))).thenReturn(Optional.empty());

    handler.execute(query);

    verify(findDonorByIdRepository).findById(Id.from(DONOR_ID));
  }

  @Test
  void execute_should_return_result_from_repository_when_donor_exists() {
    var query = new FindDonorByIdQuery(DONOR_ID);
    Donor foundDonor =
        Donor.create(
                Id.from(DONOR_ID),
                Id.from(USER_ID),
                "user@example.com",
                Instant.parse("2024-06-01T12:00:00Z"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    when(findDonorByIdRepository.findById(Id.from(DONOR_ID))).thenReturn(Optional.of(foundDonor));

    Optional<Donor> result = handler.execute(query);

    assertEquals(Optional.of(foundDonor), result);
  }

  @Test
  void execute_should_return_empty_when_repository_returns_empty() {
    var query = new FindDonorByIdQuery(DONOR_ID);
    when(findDonorByIdRepository.findById(Id.from(DONOR_ID))).thenReturn(Optional.empty());

    Optional<Donor> result = handler.execute(query);

    assertTrue(result.isEmpty());
  }
}
