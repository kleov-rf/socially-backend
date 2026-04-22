package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.DonationsOrder;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindDonationsRepository implements FindDonationsRepository {

  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;
  private final KeysetCursorCodec cursorCodec;

  @Override
  public Page<Donation> find(PaginationCriteria paginationCriteria) {
    int pageSize = paginationCriteria.size();
    String searchPattern = toSearchPattern(paginationCriteria.query());
    long totalCount = countDonations(searchPattern);
    boolean previousCursorRequest =
        Objects.nonNull(paginationCriteria.cursor())
            && cursorCodec.isPreviousCursor(paginationCriteria.cursor());
    List<DonationEntity> entities =
        fetchEntities(
            paginationCriteria.cursor(),
            pageSize + 1,
            previousCursorRequest,
            paginationCriteria.order(),
            searchPattern);
    boolean overflowItemsExist = entities.size() > pageSize;

    List<DonationEntity> pageEntities =
        getPageDonations(overflowItemsExist, entities, pageSize, previousCursorRequest);

    String nextCursor =
        getNextCursor(
            paginationCriteria.cursor(), previousCursorRequest, overflowItemsExist, pageEntities);
    String previousCursor =
        getPreviousCursor(
            paginationCriteria.cursor(), previousCursorRequest, overflowItemsExist, pageEntities);
    List<Donation> donations = pageEntities.stream().map(entityMapper::toDomain).toList();
    Metadata metadata = Metadata.create(nextCursor, previousCursor, pageSize, totalCount);

    return Page.create(donations, metadata);
  }

  private List<DonationEntity> fetchEntities(
      String cursor,
      int fetchSize,
      boolean previousCursorRequest,
      DonationsOrder order,
      String searchPattern) {
    PageRequest pageRequest = PageRequest.of(0, fetchSize);

    if (Objects.isNull(cursor)) {
      if (Objects.nonNull(searchPattern)) {
        if (order == DonationsOrder.OLDEST_FIRST) {
          return entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
              searchPattern, pageRequest);
        }
        return entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
            searchPattern, pageRequest);
      }
      if (order == DonationsOrder.OLDEST_FIRST) {
        return entityRepository.findByOrderByCreatedAtAscIdAsc(pageRequest);
      }
      return entityRepository.findByOrderByCreatedAtDescIdDesc(pageRequest);
    }

    KeysetCursor boundary = cursorCodec.decode(cursor);
    if (previousCursorRequest) {
      if (Objects.nonNull(searchPattern)) {
        if (order == DonationsOrder.OLDEST_FIRST) {
          return entityRepository.findPreviousPageForOldestFirstBySearchPattern(
              searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
        }
        return entityRepository.findPreviousPageBySearchPattern(
            searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
      }
      if (order == DonationsOrder.OLDEST_FIRST) {
        return entityRepository.findPreviousPageForOldestFirst(
            boundary.createdAt(), boundary.id(), pageRequest);
      }
      return entityRepository.findPreviousPage(boundary.createdAt(), boundary.id(), pageRequest);
    }
    if (Objects.nonNull(searchPattern)) {
      if (order == DonationsOrder.OLDEST_FIRST) {
        return entityRepository.findNextPageForOldestFirstBySearchPattern(
            searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
      }
      return entityRepository.findNextPageBySearchPattern(
          searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
    }
    if (order == DonationsOrder.OLDEST_FIRST) {
      return entityRepository.findNextPageForOldestFirst(
          boundary.createdAt(), boundary.id(), pageRequest);
    }
    return entityRepository.findNextPage(boundary.createdAt(), boundary.id(), pageRequest);
  }

  private long countDonations(String searchPattern) {
    if (Objects.isNull(searchPattern)) {
      return entityRepository.count();
    }
    return entityRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        paginationSafeTerm(searchPattern), paginationSafeTerm(searchPattern));
  }

  private static String toSearchPattern(String query) {
    if (Objects.isNull(query)) {
      return null;
    }
    return "%" + query.toLowerCase(Locale.ROOT) + "%";
  }

  private static String paginationSafeTerm(String searchPattern) {
    return searchPattern.substring(1, searchPattern.length() - 1);
  }

  private static List<DonationEntity> getPageDonations(
      boolean nextPageExists,
      List<DonationEntity> fetchedEntities,
      int pageSize,
      boolean previousCursorRequest) {
    if (fetchedEntities.isEmpty()) {
      return fetchedEntities;
    }

    if (previousCursorRequest) {
      List<DonationEntity> entitiesAscending = fetchedEntities;
      if (nextPageExists) {
        entitiesAscending = fetchedEntities.subList(0, pageSize);
      }

      List<DonationEntity> entitiesDescending = new ArrayList<>(entitiesAscending);
      Collections.reverse(entitiesDescending);
      return entitiesDescending;
    }

    if (!nextPageExists) {
      return fetchedEntities;
    }
    return fetchedEntities.subList(0, pageSize);
  }

  private String getNextCursor(
      String cursor,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      List<DonationEntity> donations) {
    if (donations.isEmpty()) {
      return null;
    }

    if (previousCursorRequest && Objects.isNull(cursor)) {
      return null;
    }

    if (!previousCursorRequest && !overflowItemsExist) {
      return null;
    }

    DonationEntity lastEntity = donations.getLast();
    return cursorCodec.encode(lastEntity.getCreatedAt(), lastEntity.getId());
  }

  private String getPreviousCursor(
      String cursor,
      boolean previousCursorRequest,
      boolean overflowItemsExist,
      List<DonationEntity> donations) {
    if (Objects.isNull(cursor) || donations.isEmpty()) {
      return null;
    }

    if (previousCursorRequest && !overflowItemsExist) {
      return null;
    }

    DonationEntity firstEntity = donations.getFirst();
    return cursorCodec.encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId());
  }
}
