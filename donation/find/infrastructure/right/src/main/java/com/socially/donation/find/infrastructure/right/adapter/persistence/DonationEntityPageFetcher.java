package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PageOrder;
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
    if (Objects.nonNull(criteria.searchPattern())) {
      if (criteria.paginationCriteria().order() == PageOrder.OLDEST_FIRST) {
        return entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
            criteria.searchPattern(), criteria.pageRequest());
      }
      return entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
          criteria.searchPattern(), criteria.pageRequest());
    }
    if (criteria.paginationCriteria().order() == PageOrder.OLDEST_FIRST) {
      return entityRepository.findByOrderByCreatedAtAscIdAsc(criteria.pageRequest());
    }
    return entityRepository.findByOrderByCreatedAtDescIdDesc(criteria.pageRequest());
  }

  private List<DonationEntity> fetchPreviousPage(FetchCriteria criteria) {
    if (Objects.nonNull(criteria.searchPattern())) {
      if (criteria.paginationCriteria().order() == PageOrder.OLDEST_FIRST) {
        return entityRepository.findPreviousPageForOldestFirstBySearchPattern(
            criteria.searchPattern(),
            criteria.boundary().createdAt(),
            criteria.boundary().id(),
            criteria.pageRequest());
      }
      return entityRepository.findPreviousPageBySearchPattern(
          criteria.searchPattern(),
          criteria.boundary().createdAt(),
          criteria.boundary().id(),
          criteria.pageRequest());
    }
    if (criteria.paginationCriteria().order() == PageOrder.OLDEST_FIRST) {
      return entityRepository.findPreviousPageForOldestFirst(
          criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
    }
    return entityRepository.findPreviousPage(
        criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
  }

  private List<DonationEntity> fetchNextPage(FetchCriteria criteria) {
    if (Objects.nonNull(criteria.searchPattern())) {
      if (criteria.paginationCriteria().order() == PageOrder.OLDEST_FIRST) {
        return entityRepository.findNextPageForOldestFirstBySearchPattern(
            criteria.searchPattern(),
            criteria.boundary().createdAt(),
            criteria.boundary().id(),
            criteria.pageRequest());
      }
      return entityRepository.findNextPageBySearchPattern(
          criteria.searchPattern(),
          criteria.boundary().createdAt(),
          criteria.boundary().id(),
          criteria.pageRequest());
    }
    if (criteria.paginationCriteria().order() == PageOrder.OLDEST_FIRST) {
      return entityRepository.findNextPageForOldestFirst(
          criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
    }
    return entityRepository.findNextPage(
        criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
  }
}
