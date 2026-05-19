package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.find.domain.proximity.ProximityReference;
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
    if (criteria.paginationCriteria().order() == PageOrder.NEAREST_FIRST) {
      return fetchInitialNearestPage(criteria);
    }

    if (Objects.isNull(criteria.searchPattern())) {
      return switch (criteria.paginationCriteria().order()) {
        case OLDEST_FIRST ->
            entityRepository.findByOrderByCreatedAtAscIdAsc(criteria.pageRequest());
        case NEWEST_FIRST ->
            entityRepository.findByOrderByCreatedAtDescIdDesc(criteria.pageRequest());
        case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
      };
    }

    return switch (criteria.paginationCriteria().order()) {
      case OLDEST_FIRST ->
          entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
              criteria.searchPattern(), criteria.pageRequest());
      case NEWEST_FIRST ->
          entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
              criteria.searchPattern(), criteria.pageRequest());
      case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
    };
  }

  private List<DonationEntity> fetchInitialNearestPage(FetchCriteria criteria) {
    ProximityReference reference = criteria.proximityReference();
    if (Objects.isNull(criteria.searchPattern())) {
      return entityRepository.findNearestFirst(
          reference.latitude(), reference.longitude(), criteria.pageRequest());
    }

    return entityRepository.findNearestFirstBySearchPattern(
        criteria.searchPattern(),
        reference.latitude(),
        reference.longitude(),
        criteria.pageRequest());
  }

  private List<DonationEntity> fetchPreviousPage(FetchCriteria criteria) {
    if (criteria.paginationCriteria().order() == PageOrder.NEAREST_FIRST) {
      return fetchPreviousNearestPage(criteria);
    }

    if (Objects.isNull(criteria.searchPattern())) {
      return switch (criteria.paginationCriteria().order()) {
        case OLDEST_FIRST ->
            entityRepository.findPreviousPageForOldestFirst(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
        case NEWEST_FIRST ->
            entityRepository.findPreviousPage(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
        case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
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
      case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
    };
  }

  private List<DonationEntity> fetchPreviousNearestPage(FetchCriteria criteria) {
    ProximityReference reference = criteria.proximityReference();
    ProximityKeysetCursor boundary = criteria.proximityBoundary();

    if (Objects.isNull(criteria.searchPattern())) {
      return entityRepository.findPreviousNearestFirstPage(
          reference.latitude(),
          reference.longitude(),
          boundary.distanceMeters(),
          boundary.id(),
          criteria.pageRequest());
    }

    return entityRepository.findPreviousNearestFirstPageBySearchPattern(
        criteria.searchPattern(),
        reference.latitude(),
        reference.longitude(),
        boundary.distanceMeters(),
        boundary.id(),
        criteria.pageRequest());
  }

  private List<DonationEntity> fetchNextPage(FetchCriteria criteria) {
    if (criteria.paginationCriteria().order() == PageOrder.NEAREST_FIRST) {
      return fetchNextNearestPage(criteria);
    }

    if (Objects.isNull(criteria.searchPattern())) {
      return switch (criteria.paginationCriteria().order()) {
        case OLDEST_FIRST ->
            entityRepository.findNextPageForOldestFirst(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
        case NEWEST_FIRST ->
            entityRepository.findNextPage(
                criteria.boundary().createdAt(), criteria.boundary().id(), criteria.pageRequest());
        case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
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
      case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
    };
  }

  private List<DonationEntity> fetchNextNearestPage(FetchCriteria criteria) {
    ProximityReference reference = criteria.proximityReference();
    ProximityKeysetCursor boundary = criteria.proximityBoundary();

    if (Objects.isNull(criteria.searchPattern())) {
      return entityRepository.findNextNearestFirstPage(
          reference.latitude(),
          reference.longitude(),
          boundary.distanceMeters(),
          boundary.id(),
          criteria.pageRequest());
    }

    return entityRepository.findNextNearestFirstPageBySearchPattern(
        criteria.searchPattern(),
        reference.latitude(),
        reference.longitude(),
        boundary.distanceMeters(),
        boundary.id(),
        criteria.pageRequest());
  }
}
