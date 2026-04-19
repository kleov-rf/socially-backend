package com.socially.donation.find.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
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
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class JpaFindDonationsRepositoryTest {

  private static final String DONATION_ID_1 = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONATION_ID_2 = "550e8400-e29b-41d4-a716-446655440001";
  private static final String DONATION_ID_3 = "550e8400-e29b-41d4-a716-446655440002";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private DonationEntityRepository entityRepository;

  @Mock private DonationEntityMapper entityMapper;

  @Mock private KeysetCursorCodec cursorCodec;

  @InjectMocks private JpaFindDonationsRepository sut;

  @Test
  void find_should_call_to_decode_cursor() {
    PaginationCriteria query = PaginationCriteria.create("cursor-token", 2);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursorCodec.CursorBoundary boundary =
        new KeysetCursorCodec.CursorBoundary(CREATED_AT, boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(CREATED_AT, boundaryId, PageRequest.of(0, 3)))
        .thenReturn(List.of());

    sut.find(query);

    verify(cursorCodec).decode("cursor-token");
  }

  @Test
  void find_should_call_find_with_cursor_and_one_more_item_than_given_page_size() {
    int pageSize = 2;
    PaginationCriteria query = PaginationCriteria.create("cursor-token", pageSize);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursorCodec.CursorBoundary boundary =
        new KeysetCursorCodec.CursorBoundary(CREATED_AT, boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository).findNextPage(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_find_with_one_more_item_than_given_page_size_when_cursor_not_present() {
    int pageSize = 2;
    PaginationCriteria query = PaginationCriteria.create(null, pageSize);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository).findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_to_encode_last_item_if_next_page_exists() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity overflowEntity =
        donationEntity(DONATION_ID_3, "Title 3", "Description 3", CREATED_AT.minusSeconds(2));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encode(secondEntity.getCreatedAt(), secondEntity.getId()))
        .thenReturn("next-cursor");

    sut.find(query);

    verify(cursorCodec).encode(secondEntity.getCreatedAt(), secondEntity.getId());
  }

  @Test
  void find_should_not_call_to_encode_if_next_page_does_not_exist() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    sut.find(query);

    verify(cursorCodec, never()).encode(firstEntity.getCreatedAt(), firstEntity.getId());
    verify(cursorCodec, never()).encode(secondEntity.getCreatedAt(), secondEntity.getId());
  }

  @Test
  void find_should_call_entity_mapper_with_given_page_size_times_the_retrieved_items() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity overflowEntity =
        donationEntity(DONATION_ID_3, "Title 3", "Description 3", CREATED_AT.minusSeconds(2));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encode(secondEntity.getCreatedAt(), secondEntity.getId()))
        .thenReturn("next-cursor");

    sut.find(query);

    verify(entityMapper).toDomain(firstEntity);
    verify(entityMapper).toDomain(secondEntity);
    verify(entityMapper, never()).toDomain(overflowEntity);
    verify(entityMapper, times(2)).toDomain(any(DonationEntity.class));
  }

  @Test
  void find_should_return_mapped_donations() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    Donation mappedFirstDonation = mappedDonation(firstEntity);
    Donation mappedSecondDonation = mappedDonation(secondEntity);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedFirstDonation);
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedSecondDonation);

    Page<Donation> result = sut.find(query);

    assertEquals(List.of(mappedFirstDonation, mappedSecondDonation), result.items());
  }

  @Test
  void find_should_return_metadata_next_cursor_when_next_page_exists() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity overflowEntity =
        donationEntity(DONATION_ID_3, "Title 3", "Description 3", CREATED_AT.minusSeconds(2));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encode(secondEntity.getCreatedAt(), secondEntity.getId()))
        .thenReturn("next-cursor");

    Page<Donation> result = sut.find(query);

    assertEquals("next-cursor", result.metadata().nextCursor());
  }

  @Test
  void find_should_return_metadata_next_cursor_as_null_when_next_page_not_exists() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    Page<Donation> result = sut.find(query);

    assertNull(result.metadata().nextCursor());
  }

  @Test
  void find_should_return_metadata_next_page_exists() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity overflowEntity =
        donationEntity(DONATION_ID_3, "Title 3", "Description 3", CREATED_AT.minusSeconds(2));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encode(secondEntity.getCreatedAt(), secondEntity.getId()))
        .thenReturn("next-cursor");

    Page<Donation> result = sut.find(query);

    assertTrue(result.metadata().hasNext());
  }

  @Test
  void find_should_return_metadata_received_page_size() {
    PaginationCriteria query = PaginationCriteria.create(null, 2);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 3)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    Page<Donation> result = sut.find(query);

    assertEquals(2, result.metadata().size());
    assertFalse(result.metadata().hasNext());
  }

  private static DonationEntity donationEntity(
      String id, String title, String description, Instant createdAt) {
    return new DonationEntity(Id.from(id).value(), title, description, createdAt, createdAt);
  }

  private static Donation mappedDonation(DonationEntity entity) {
    return Donation.create(
        Id.from(entity.getId().toString()),
        Title.from(entity.getTitle()),
        Description.from(entity.getDescription()),
        entity.getCreatedAt(),
        entity.getLastUpdatedAt());
  }
}
