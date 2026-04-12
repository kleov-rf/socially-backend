package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFindDonationsRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private DonationEntityRepository entityRepository;

  @Mock private DonationEntityMapper entityMapper;

  @InjectMocks private JpaFindDonationsRepository sut;

  @Test
  void find_should_call_entity_repository_find_all() {
    sut.find();

    verify(entityRepository).findAll();
  }

  @Test
  void find_should_call_mapper_with_each_retrieved_donation_entity() {
    DonationEntity firstEntity =
        new DonationEntity(
            Id.from(DONATION_ID).value(),
            "First Entity Title",
            "First Entity Description",
            CREATED_AT,
            CREATED_AT);
    DonationEntity secondEntity =
        new DonationEntity(
            Id.from("550e8400-e29b-41d4-a716-446655440001").value(),
            "Second Entity Title",
            "Second Entity Description",
            CREATED_AT,
            CREATED_AT);
    when(entityRepository.findAll()).thenReturn(List.of(firstEntity, secondEntity));

    sut.find();

    verify(entityMapper).toDomain(firstEntity);
    verify(entityMapper).toDomain(secondEntity);
  }

  @Test
  void find_should_return_mapped_donations() {
    UUID entityId = Id.from(DONATION_ID).value();
    DonationEntity entity =
        new DonationEntity(entityId, "Entity Title", "Entity Description", CREATED_AT, CREATED_AT);
    when(entityRepository.findAll()).thenReturn(List.of(entity));

    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Entity Title"),
            Description.from("Entity Description"),
            CREATED_AT,
            CREATED_AT);
    when(entityMapper.toDomain(entity)).thenReturn(mappedDonation);

    List<Donation> result = sut.find();

    assertEquals(List.of(mappedDonation), result);
  }
}
