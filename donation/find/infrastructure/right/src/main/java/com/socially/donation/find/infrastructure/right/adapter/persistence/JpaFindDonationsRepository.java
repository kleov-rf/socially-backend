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
    String searchPattern = null;
    if (Objects.nonNull(paginationCriteria.query())) {
      searchPattern = "%" + paginationCriteria.query().toLowerCase(Locale.ROOT) + "%";
    }
    long totalCount;
    if (Objects.isNull(searchPattern)) {
      totalCount = entityRepository.countByDeletedAtIsNull();
    } else {
      totalCount = entityRepository.countBySearchPattern(searchPattern);
    }
    boolean previousCursorRequest =
        Objects.nonNull(paginationCriteria.cursor())
            && cursorCodec.isPreviousCursor(paginationCriteria.cursor());
    List<DonationEntity> entities;
    PageRequest pageRequest = PageRequest.of(0, paginationCriteria.size() + 1);

    if (Objects.isNull(paginationCriteria.cursor())) {
      if (Objects.nonNull(searchPattern)) {
        if (paginationCriteria.order() == DonationsOrder.OLDEST_FIRST) {
          entities =
              entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
                  searchPattern, pageRequest);
        } else {
          entities =
              entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
                  searchPattern, pageRequest);
        }
      } else if (paginationCriteria.order() == DonationsOrder.OLDEST_FIRST) {
        entities = entityRepository.findByOrderByCreatedAtAscIdAsc(pageRequest);
      } else {
        entities = entityRepository.findByOrderByCreatedAtDescIdDesc(pageRequest);
      }
    } else {
      KeysetCursor boundary = cursorCodec.decode(paginationCriteria.cursor());
      if (previousCursorRequest) {
        if (Objects.nonNull(searchPattern)) {
          if (paginationCriteria.order() == DonationsOrder.OLDEST_FIRST) {
            entities =
                entityRepository.findPreviousPageForOldestFirstBySearchPattern(
                    searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
          } else {
            entities =
                entityRepository.findPreviousPageBySearchPattern(
                    searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
          }
        } else if (paginationCriteria.order() == DonationsOrder.OLDEST_FIRST) {
          entities =
              entityRepository.findPreviousPageForOldestFirst(
                  boundary.createdAt(), boundary.id(), pageRequest);
        } else {
          entities =
              entityRepository.findPreviousPage(boundary.createdAt(), boundary.id(), pageRequest);
        }
      } else if (Objects.nonNull(searchPattern)) {
        if (paginationCriteria.order() == DonationsOrder.OLDEST_FIRST) {
          entities =
              entityRepository.findNextPageForOldestFirstBySearchPattern(
                  searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
        } else {
          entities =
              entityRepository.findNextPageBySearchPattern(
                  searchPattern, boundary.createdAt(), boundary.id(), pageRequest);
        }
      } else if (paginationCriteria.order() == DonationsOrder.OLDEST_FIRST) {
        entities =
            entityRepository.findNextPageForOldestFirst(
                boundary.createdAt(), boundary.id(), pageRequest);
      } else {
        entities = entityRepository.findNextPage(boundary.createdAt(), boundary.id(), pageRequest);
      }
    }

    boolean overflowItemsExist = entities.size() > paginationCriteria.size();

    List<DonationEntity> pageEntities;
    if (entities.isEmpty()) {
      pageEntities = entities;
    } else if (previousCursorRequest) {
      List<DonationEntity> entitiesAscending = entities;
      if (overflowItemsExist) {
        entitiesAscending = entities.subList(0, paginationCriteria.size());
      }

      List<DonationEntity> entitiesDescending = new ArrayList<>(entitiesAscending);
      Collections.reverse(entitiesDescending);
      pageEntities = entitiesDescending;
    } else if (!overflowItemsExist) {
      pageEntities = entities;
    } else {
      pageEntities = entities.subList(0, paginationCriteria.size());
    }

    String nextCursor = null;
    if (!pageEntities.isEmpty()) {
      if (!previousCursorRequest || Objects.nonNull(paginationCriteria.cursor())) {
        if (previousCursorRequest || overflowItemsExist) {
          DonationEntity lastEntity = pageEntities.getLast();
          nextCursor = cursorCodec.encode(lastEntity.getCreatedAt(), lastEntity.getId());
        }
      }
    }

    String previousCursor = null;
    if (Objects.nonNull(paginationCriteria.cursor()) && !pageEntities.isEmpty()) {
      if (!previousCursorRequest || overflowItemsExist) {
        DonationEntity firstEntity = pageEntities.getFirst();
        previousCursor =
            cursorCodec.encodePrevious(firstEntity.getCreatedAt(), firstEntity.getId());
      }
    }

    List<Donation> donations = pageEntities.stream().map(entityMapper::toDomain).toList();
    Metadata metadata =
        Metadata.create(nextCursor, previousCursor, paginationCriteria.size(), totalCount);

    return Page.create(donations, metadata);
  }
}
