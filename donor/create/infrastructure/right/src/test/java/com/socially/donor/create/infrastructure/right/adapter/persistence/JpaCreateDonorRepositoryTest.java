package com.socially.donor.create.infrastructure.right.adapter.persistence;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.DonorEntityRepository;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity.DonorEntity;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper.DonorEntityMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaCreateDonorRepositoryTest {

  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private DonorEntityRepository entityRepository;
  @Mock private DonorEntityMapper entityMapper;

  @InjectMocks private JpaCreateDonorRepository sut;

  private static Donor donor() {
    return Donor.create(
        Id.from(DONOR_ID),
        Id.from(USER_ID),
        "donor@example.com",
        "Jane",
        "Doe",
        CREATED_AT);
  }

  private static DonorEntity mappedEntity() {
    return DonorEntity.create(
        UUID.fromString(DONOR_ID),
        UUID.fromString(USER_ID),
        "donor@example.com",
        "Jane",
        "Doe",
        CREATED_AT);
  }

  @Test
  void create_should_call_entity_mapper_toEntity_with_received_donor() {
    Donor donor = donor();
    DonorEntity entity = mappedEntity();
    when(entityMapper.toEntity(donor)).thenReturn(entity);

    sut.create(donor);

    verify(entityMapper).toEntity(eq(donor));
  }

  @Test
  void create_should_call_entity_repository_save_with_entity_returned_by_mapper() {
    Donor donor = donor();
    DonorEntity entity = mappedEntity();
    when(entityMapper.toEntity(donor)).thenReturn(entity);

    sut.create(donor);

    verify(entityRepository).save(eq(entity));
  }
}
