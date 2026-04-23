package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonationEntityPageFetcher implements PageFetcher {
  private final DonationEntityRepository entityRepository;

  @Override
  public List<DonationEntity> fetch(FetchCriteria criteria) {
    if (Objects.isNull(criteria.paginationCriteria().cursor())) {
      return fetchInitialPage(criteria);
    }
    if (criteria.previousCursorRequest()) {
      return fetchPreviousPage(criteria);
    }
    return fetchNextPage(criteria);
  }

  private List<DonationEntity> fetchInitialPage(FetchCriteria criteria) {
    if (Objects.isNull(criteria.searchPattern())) {
      return switch (criteria.paginationCriteria().order()) {
        case OLDEST_FIRST ->
            entityRepository.findByOrderByCreatedAtAscIdAsc(criteria.pageRequest());
        case NEWEST_FIRST ->
            entityRepository.findByOrderByCreatedAtDescIdDesc(criteria.pageRequest());
      };
    }

    return switch (criteria.paginationCriteria().order()) {
      case OLDEST_FIRST ->
          entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
              criteria.searchPattern(), criteria.pageRequest());
      case NEWEST_FIRST ->
          entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
              criteria.searchPattern(), criteria.pageRequest());
    };
  }

  private List<DonationEntity> fetchPreviousPage(FetchCriteria criteria) {
    if (Objects.isNull(criteria.searchPattern())) {
      return switch (criteria.paginationCriteria().order()) {
        case OLDEST_FIRST ->
            entityRepository.findPreviousPageForOldestFirst(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
        case NEWEST_FIRST ->
            entityRepository.findPreviousPage(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
      };
    }
    return switch (criteria.paginationCriteria().order()) {
      case OLDEST_FIRST ->
          entityRepository.findPreviousPageForOldestFirstBySearchPattern(
              criteria.searchPattern(),
              criteria.boundary().createdAt(),
              criteria.boundary().id(),
              criteria.pageRequest());
      case NEWEST_FIRST ->
          entityRepository.findPreviousPageBySearchPattern(
              criteria.searchPattern(),
              criteria.boundary().createdAt(),
              criteria.boundary().id(),
              criteria.pageRequest());
    };
  }

  private List<DonationEntity> fetchNextPage(FetchCriteria criteria) {
    if (Objects.isNull(criteria.searchPattern())) {
      return switch (criteria.paginationCriteria().order()) {
        case OLDEST_FIRST ->
            entityRepository.findNextPageForOldestFirst(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
        case NEWEST_FIRST ->
            entityRepository.findNextPage(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
      };
    }
    return switch (criteria.paginationCriteria().order()) {
      case OLDEST_FIRST ->
          entityRepository.findNextPageForOldestFirstBySearchPattern(
              criteria.searchPattern(),
              criteria.boundary().createdAt(),
              criteria.boundary().id(),
              criteria.pageRequest());
      case NEWEST_FIRST ->
          entityRepository.findNextPageBySearchPattern(
              criteria.searchPattern(),
              criteria.boundary().createdAt(),
              criteria.boundary().id(),
              criteria.pageRequest());
    };
  }
}
