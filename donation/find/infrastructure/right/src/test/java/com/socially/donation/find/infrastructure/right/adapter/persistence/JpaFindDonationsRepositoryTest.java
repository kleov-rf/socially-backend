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

import com.socially.donation.find.domain.pagination.DonationsOrder;
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
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(CREATED_AT, boundaryId, PageRequest.of(0, 6)))
        .thenReturn(List.of());

    sut.find(query);

    verify(cursorCodec).decode("cursor-token");
  }

  @Test
  void find_should_call_find_previous_page_when_cursor_is_previous_cursor() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(
            "previous-cursor-token", pageSize, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.isPreviousCursor("previous-cursor-token")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor-token")).thenReturn(boundary);
    when(entityRepository.findPreviousPage(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository)
        .findPreviousPage(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_return_items_in_descending_creation_date_when_cursor_is_previous_cursor() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(
            "previous-cursor-token", pageSize, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_3).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT.minusSeconds(3), boundaryId);
    DonationEntity oldestEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440010",
            "Title 1",
            "Description 1",
            CREATED_AT.minusSeconds(4));
    DonationEntity secondOldestEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440011",
            "Title 2",
            "Description 2",
            CREATED_AT.minusSeconds(3));
    DonationEntity middleEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440012",
            "Title 3",
            "Description 3",
            CREATED_AT.minusSeconds(2));
    DonationEntity secondNewestEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440013",
            "Title 4",
            "Description 4",
            CREATED_AT.minusSeconds(1));
    DonationEntity newestEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440014", "Title 5", "Description 5", CREATED_AT);
    when(cursorCodec.isPreviousCursor("previous-cursor-token")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor-token")).thenReturn(boundary);
    when(entityRepository.findPreviousPage(
            CREATED_AT.minusSeconds(3), boundaryId, PageRequest.of(0, pageSize + 1)))
        .thenReturn(
            List.of(
                oldestEntity, secondOldestEntity, middleEntity, secondNewestEntity, newestEntity));
    when(entityMapper.toDomain(oldestEntity)).thenReturn(mappedDonation(oldestEntity));
    when(entityMapper.toDomain(secondOldestEntity)).thenReturn(mappedDonation(secondOldestEntity));
    when(entityMapper.toDomain(middleEntity)).thenReturn(mappedDonation(middleEntity));
    when(entityMapper.toDomain(secondNewestEntity)).thenReturn(mappedDonation(secondNewestEntity));
    when(entityMapper.toDomain(newestEntity)).thenReturn(mappedDonation(newestEntity));

    Page<Donation> result = sut.find(query);

    assertEquals(
        List.of(
            mappedDonation(newestEntity),
            mappedDonation(secondNewestEntity),
            mappedDonation(middleEntity),
            mappedDonation(secondOldestEntity),
            mappedDonation(oldestEntity)),
        result.items());
  }

  @Test
  void find_should_call_find_with_cursor_and_one_more_item_than_given_page_size() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", pageSize, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository).findNextPage(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_find_with_one_more_item_than_given_page_size_when_cursor_not_present() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(null, pageSize, PaginationCriteria.DEFAULT_ORDER, null);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository).findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_oldest_first_query_when_oldest_order_is_requested() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(null, pageSize, DonationsOrder.OLDEST_FIRST, null);
    when(entityRepository.findByOrderByCreatedAtAscIdAsc(PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository).findByOrderByCreatedAtAscIdAsc(PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_oldest_first_next_page_query_when_next_cursor_is_requested() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", pageSize, DonationsOrder.OLDEST_FIRST, null);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPageForOldestFirst(CREATED_AT, boundaryId, PageRequest.of(0, 6)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository)
        .findNextPageForOldestFirst(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_oldest_first_previous_page_query_when_previous_cursor_is_requested() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(
            "previous-cursor-token", pageSize, DonationsOrder.OLDEST_FIRST, null);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.isPreviousCursor("previous-cursor-token")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor-token")).thenReturn(boundary);
    when(entityRepository.findPreviousPageForOldestFirst(
            CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository)
        .findPreviousPageForOldestFirst(CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_search_query_when_query_is_present_and_order_is_newest() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(null, pageSize, DonationsOrder.NEWEST_FIRST, "school");
    when(entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
            "%school%", PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository)
        .findBySearchPatternOrderByCreatedAtDescIdDesc("%school%", PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_oldest_first_search_query_when_query_is_present() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create(null, pageSize, DonationsOrder.OLDEST_FIRST, "school");
    when(entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
            "%school%", PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository)
        .findBySearchPatternOrderByCreatedAtAscIdAsc("%school%", PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_call_search_next_page_query_when_cursor_is_present() {
    int pageSize = 5;
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", pageSize, DonationsOrder.NEWEST_FIRST, "school");
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPageBySearchPattern(
            "%school%", CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1)))
        .thenReturn(List.of());

    sut.find(query);

    verify(entityRepository)
        .findNextPageBySearchPattern(
            "%school%", CREATED_AT, boundaryId, PageRequest.of(0, pageSize + 1));
  }

  @Test
  void find_should_map_total_count() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(entityRepository.countByDeletedAtIsNull()).thenReturn(50L);

    Page<Donation> result = sut.find(query);

    assertEquals(50L, result.metadata().totalCount());
  }

  @Test
  void find_should_call_to_encode_last_item_if_next_page_exists() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity thirdEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440020",
            "Title 3",
            "Description 3",
            CREATED_AT.minusSeconds(2));
    DonationEntity fourthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440021",
            "Title 4",
            "Description 4",
            CREATED_AT.minusSeconds(3));
    DonationEntity fifthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440022",
            "Title 5",
            "Description 5",
            CREATED_AT.minusSeconds(4));
    DonationEntity overflowEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440023",
            "Title 6",
            "Description 6",
            CREATED_AT.minusSeconds(5));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(
            List.of(
                firstEntity, secondEntity, thirdEntity, fourthEntity, fifthEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(entityMapper.toDomain(thirdEntity)).thenReturn(mappedDonation(thirdEntity));
    when(entityMapper.toDomain(fourthEntity)).thenReturn(mappedDonation(fourthEntity));
    when(entityMapper.toDomain(fifthEntity)).thenReturn(mappedDonation(fifthEntity));
    when(cursorCodec.encode(fifthEntity.getCreatedAt(), fifthEntity.getId()))
        .thenReturn("next-cursor");

    sut.find(query);

    verify(cursorCodec).encode(fifthEntity.getCreatedAt(), fifthEntity.getId());
  }

  @Test
  void find_should_not_call_to_encode_if_next_page_does_not_exist() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    sut.find(query);

    verify(cursorCodec, never()).encode(firstEntity.getCreatedAt(), firstEntity.getId());
    verify(cursorCodec, never()).encode(secondEntity.getCreatedAt(), secondEntity.getId());
  }

  @Test
  void find_should_call_to_encode_first_item_if_cursor_is_present_and_donations_found() {
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_3).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT.minusSeconds(3), boundaryId);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(
            boundary.createdAt(), boundary.id(), PageRequest.of(0, query.size() + 1)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId()))
        .thenReturn("previous-cursor");

    sut.find(query);

    verify(cursorCodec).encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId());
  }

  @Test
  void find_should_return_empty_list_if_no_donations_were_found() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of());

    Page<Donation> result = sut.find(query);

    assertTrue(result.items().isEmpty());
  }

  @Test
  void find_should_return_null_previous_and_next_cursors_if_no_donations_were_found() {
    PaginationCriteria query =
        PaginationCriteria.create(
            "previous-cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_1).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT, boundaryId);
    when(cursorCodec.isPreviousCursor("previous-cursor-token")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor-token")).thenReturn(boundary);
    when(entityRepository.findPreviousPage(CREATED_AT, boundaryId, PageRequest.of(0, 6)))
        .thenReturn(List.of());

    Page<Donation> result = sut.find(query);

    assertNull(result.metadata().previousCursor());
    assertNull(result.metadata().nextCursor());
  }

  @Test
  void find_should_call_entity_mapper_with_given_page_size_times_the_retrieved_items() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity thirdEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440024",
            "Title 3",
            "Description 3",
            CREATED_AT.minusSeconds(2));
    DonationEntity fourthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440025",
            "Title 4",
            "Description 4",
            CREATED_AT.minusSeconds(3));
    DonationEntity fifthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440026",
            "Title 5",
            "Description 5",
            CREATED_AT.minusSeconds(4));
    DonationEntity overflowEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440027",
            "Title 6",
            "Description 6",
            CREATED_AT.minusSeconds(5));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(
            List.of(
                firstEntity, secondEntity, thirdEntity, fourthEntity, fifthEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(entityMapper.toDomain(thirdEntity)).thenReturn(mappedDonation(thirdEntity));
    when(entityMapper.toDomain(fourthEntity)).thenReturn(mappedDonation(fourthEntity));
    when(entityMapper.toDomain(fifthEntity)).thenReturn(mappedDonation(fifthEntity));
    when(cursorCodec.encode(fifthEntity.getCreatedAt(), fifthEntity.getId()))
        .thenReturn("next-cursor");

    sut.find(query);

    verify(entityMapper).toDomain(firstEntity);
    verify(entityMapper).toDomain(secondEntity);
    verify(entityMapper).toDomain(thirdEntity);
    verify(entityMapper).toDomain(fourthEntity);
    verify(entityMapper).toDomain(fifthEntity);
    verify(entityMapper, never()).toDomain(overflowEntity);
    verify(entityMapper, times(5)).toDomain(any(DonationEntity.class));
  }

  @Test
  void find_should_return_mapped_donations() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    Donation mappedFirstDonation = mappedDonation(firstEntity);
    Donation mappedSecondDonation = mappedDonation(secondEntity);
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedFirstDonation);
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedSecondDonation);

    Page<Donation> result = sut.find(query);

    assertEquals(List.of(mappedFirstDonation, mappedSecondDonation), result.items());
  }

  @Test
  void find_should_return_metadata_next_cursor_when_next_page_exists() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity thirdEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440028",
            "Title 3",
            "Description 3",
            CREATED_AT.minusSeconds(2));
    DonationEntity fourthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440029",
            "Title 4",
            "Description 4",
            CREATED_AT.minusSeconds(3));
    DonationEntity fifthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440030",
            "Title 5",
            "Description 5",
            CREATED_AT.minusSeconds(4));
    DonationEntity overflowEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440031",
            "Title 6",
            "Description 6",
            CREATED_AT.minusSeconds(5));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(
            List.of(
                firstEntity, secondEntity, thirdEntity, fourthEntity, fifthEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(entityMapper.toDomain(thirdEntity)).thenReturn(mappedDonation(thirdEntity));
    when(entityMapper.toDomain(fourthEntity)).thenReturn(mappedDonation(fourthEntity));
    when(entityMapper.toDomain(fifthEntity)).thenReturn(mappedDonation(fifthEntity));
    when(cursorCodec.encode(fifthEntity.getCreatedAt(), fifthEntity.getId()))
        .thenReturn("next-cursor");

    Page<Donation> result = sut.find(query);

    assertEquals("next-cursor", result.metadata().nextCursor());
  }

  @Test
  void find_should_return_metadata_next_cursor_as_null_when_next_page_not_exists() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    Page<Donation> result = sut.find(query);

    assertNull(result.metadata().nextCursor());
  }

  @Test
  void find_should_return_metadata_next_page_exists() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    DonationEntity thirdEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440032",
            "Title 3",
            "Description 3",
            CREATED_AT.minusSeconds(2));
    DonationEntity fourthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440033",
            "Title 4",
            "Description 4",
            CREATED_AT.minusSeconds(3));
    DonationEntity fifthEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440034",
            "Title 5",
            "Description 5",
            CREATED_AT.minusSeconds(4));
    DonationEntity overflowEntity =
        donationEntity(
            "550e8400-e29b-41d4-a716-446655440035",
            "Title 6",
            "Description 6",
            CREATED_AT.minusSeconds(5));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(
            List.of(
                firstEntity, secondEntity, thirdEntity, fourthEntity, fifthEntity, overflowEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(entityMapper.toDomain(thirdEntity)).thenReturn(mappedDonation(thirdEntity));
    when(entityMapper.toDomain(fourthEntity)).thenReturn(mappedDonation(fourthEntity));
    when(entityMapper.toDomain(fifthEntity)).thenReturn(mappedDonation(fifthEntity));
    when(cursorCodec.encode(fifthEntity.getCreatedAt(), fifthEntity.getId()))
        .thenReturn("next-cursor");

    Page<Donation> result = sut.find(query);

    assertTrue(result.metadata().hasNext());
  }

  @Test
  void
      find_should_return_metadata_next_cursor_when_cursor_is_previous_cursor_and_donations_found() {
    PaginationCriteria query =
        PaginationCriteria.create(
            "previous-cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_3).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT.minusSeconds(3), boundaryId);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(cursorCodec.isPreviousCursor("previous-cursor-token")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor-token")).thenReturn(boundary);
    when(entityRepository.findPreviousPage(
            boundary.createdAt(), boundary.id(), PageRequest.of(0, query.size() + 1)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encode(firstEntity.getCreatedAt(), firstEntity.getId()))
        .thenReturn("next-cursor");

    Page<Donation> result = sut.find(query);

    assertEquals("next-cursor", result.metadata().nextCursor());
    assertTrue(result.metadata().hasNext());
  }

  @Test
  void find_should_return_metadata_previous_cursor_when_cursor_is_present() {
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_3).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT.minusSeconds(3), boundaryId);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(
            boundary.createdAt(), boundary.id(), PageRequest.of(0, query.size() + 1)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId()))
        .thenReturn("previous-cursor");

    Page<Donation> result = sut.find(query);

    assertEquals("previous-cursor", result.metadata().previousCursor());
    assertTrue(result.metadata().hasPrevious());
  }

  @Test
  void
      find_should_return_metadata_previous_cursor_as_null_when_cursor_is_previous_and_no_newer_page_exists() {
    PaginationCriteria query =
        PaginationCriteria.create(
            "previous-cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_3).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT.minusSeconds(3), boundaryId);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(cursorCodec.isPreviousCursor("previous-cursor-token")).thenReturn(true);
    when(cursorCodec.decode("previous-cursor-token")).thenReturn(boundary);
    when(entityRepository.findPreviousPage(
            boundary.createdAt(), boundary.id(), PageRequest.of(0, query.size() + 1)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));
    when(cursorCodec.encode(firstEntity.getCreatedAt(), firstEntity.getId()))
        .thenReturn("next-cursor");

    Page<Donation> result = sut.find(query);

    assertNull(result.metadata().previousCursor());
    assertFalse(result.metadata().hasPrevious());
  }

  @Test
  void find_should_return_metadata_previous_cursor_as_null_when_cursor_is_not_present() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    Page<Donation> result = sut.find(query);

    assertNull(result.metadata().previousCursor());
    assertFalse(result.metadata().hasPrevious());
  }

  @Test
  void find_should_return_metadata_previous_cursor_as_null_when_no_donations_found() {
    PaginationCriteria query =
        PaginationCriteria.create("cursor-token", 5, PaginationCriteria.DEFAULT_ORDER, null);
    UUID boundaryId = Id.from(DONATION_ID_3).value();
    KeysetCursor boundary = new KeysetCursor(CREATED_AT.minusSeconds(3), boundaryId);
    when(cursorCodec.decode("cursor-token")).thenReturn(boundary);
    when(entityRepository.findNextPage(
            boundary.createdAt(), boundary.id(), PageRequest.of(0, query.size() + 1)))
        .thenReturn(List.of());

    Page<Donation> result = sut.find(query);

    assertNull(result.metadata().previousCursor());
    assertFalse(result.metadata().hasPrevious());
  }

  @Test
  void find_should_return_metadata_received_page_size() {
    PaginationCriteria query =
        PaginationCriteria.create(null, 5, PaginationCriteria.DEFAULT_ORDER, null);
    DonationEntity firstEntity =
        donationEntity(DONATION_ID_1, "Title 1", "Description 1", CREATED_AT);
    DonationEntity secondEntity =
        donationEntity(DONATION_ID_2, "Title 2", "Description 2", CREATED_AT.minusSeconds(1));
    when(entityRepository.findByOrderByCreatedAtDescIdDesc(PageRequest.of(0, 6)))
        .thenReturn(List.of(firstEntity, secondEntity));
    when(entityMapper.toDomain(firstEntity)).thenReturn(mappedDonation(firstEntity));
    when(entityMapper.toDomain(secondEntity)).thenReturn(mappedDonation(secondEntity));

    Page<Donation> result = sut.find(query);

    assertEquals(5, result.metadata().size());
    assertFalse(result.metadata().hasNext());
  }

  private static DonationEntity donationEntity(
      String id, String title, String description, Instant createdAt) {
    return DonationEntity.create(Id.from(id).value(), title, description, createdAt, createdAt);
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
