package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.PageOrder;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonationEntityPageFetcher implements PageFetcher {
  private final DonationEntityRepository entityRepository;

  @Override
  public List<DonationEntity> fetch(FetchCriteria criteria) {
    if (criteria.paginationCriteria().cursor().isEmpty()) {
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

    return criteria
        .searchPattern()
        .map(
            pattern ->
                switch (criteria.paginationCriteria().order()) {
                  case OLDEST_FIRST ->
                      entityRepository.findBySearchPatternOrderByCreatedAtAscIdAsc(
                          pattern, criteria.pageRequest());
                  case NEWEST_FIRST ->
                      entityRepository.findBySearchPatternOrderByCreatedAtDescIdDesc(
                          pattern, criteria.pageRequest());
                  case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
                })
        .orElseGet(
            () ->
                switch (criteria.paginationCriteria().order()) {
                  case OLDEST_FIRST ->
                      entityRepository.findByOrderByCreatedAtAscIdAsc(criteria.pageRequest());
                  case NEWEST_FIRST ->
                      entityRepository.findByOrderByCreatedAtDescIdDesc(criteria.pageRequest());
                  case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
                });
  }

  private List<DonationEntity> fetchInitialNearestPage(FetchCriteria criteria) {
    return criteria
        .proximityReference()
        .map(
            reference ->
                criteria
                    .searchPattern()
                    .map(
                        pattern ->
                            entityRepository.findNearestFirstBySearchPattern(
                                pattern,
                                reference.latitude(),
                                reference.longitude(),
                                criteria.pageRequest()))
                    .orElseGet(
                        () ->
                            entityRepository.findNearestFirst(
                                reference.latitude(),
                                reference.longitude(),
                                criteria.pageRequest())))
        .orElseGet(List::of);
  }

  private List<DonationEntity> fetchPreviousPage(FetchCriteria criteria) {
    if (criteria.paginationCriteria().order() == PageOrder.NEAREST_FIRST) {
      return fetchPreviousNearestPage(criteria);
    }

    return criteria
        .boundary()
        .map(
            boundary ->
                criteria
                    .searchPattern()
                    .map(
                        pattern ->
                            switch (criteria.paginationCriteria().order()) {
                              case OLDEST_FIRST ->
                                  entityRepository.findPreviousPageForOldestFirstBySearchPattern(
                                      pattern,
                                      boundary.createdAt(),
                                      boundary.id(),
                                      criteria.pageRequest());
                              case NEWEST_FIRST ->
                                  entityRepository.findPreviousPageBySearchPattern(
                                      pattern,
                                      boundary.createdAt(),
                                      boundary.id(),
                                      criteria.pageRequest());
                              case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
                            })
                    .orElseGet(
                        () ->
                            switch (criteria.paginationCriteria().order()) {
                              case OLDEST_FIRST ->
                                  entityRepository.findPreviousPageForOldestFirst(
                                      boundary.createdAt(), boundary.id(), criteria.pageRequest());
                              case NEWEST_FIRST ->
                                  entityRepository.findPreviousPage(
                                      boundary.createdAt(), boundary.id(), criteria.pageRequest());
                              case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
                            }))
        .orElseGet(List::of);
  }

  private List<DonationEntity> fetchPreviousNearestPage(FetchCriteria criteria) {
    return criteria
        .proximityReference()
        .flatMap(
            reference ->
                criteria
                    .proximityBoundary()
                    .map(
                        boundary ->
                            criteria
                                .searchPattern()
                                .map(
                                    pattern ->
                                        entityRepository
                                            .findPreviousNearestFirstPageBySearchPattern(
                                                pattern,
                                                reference.latitude(),
                                                reference.longitude(),
                                                boundary.distanceMeters(),
                                                boundary.id(),
                                                criteria.pageRequest()))
                                .orElseGet(
                                    () ->
                                        entityRepository.findPreviousNearestFirstPage(
                                            reference.latitude(),
                                            reference.longitude(),
                                            boundary.distanceMeters(),
                                            boundary.id(),
                                            criteria.pageRequest()))))
        .orElseGet(List::of);
  }

  private List<DonationEntity> fetchNextPage(FetchCriteria criteria) {
    if (criteria.paginationCriteria().order() == PageOrder.NEAREST_FIRST) {
      return fetchNextNearestPage(criteria);
    }

    return criteria
        .boundary()
        .map(
            boundary ->
                criteria
                    .searchPattern()
                    .map(
                        pattern ->
                            switch (criteria.paginationCriteria().order()) {
                              case OLDEST_FIRST ->
                                  entityRepository.findNextPageForOldestFirstBySearchPattern(
                                      pattern,
                                      boundary.createdAt(),
                                      boundary.id(),
                                      criteria.pageRequest());
                              case NEWEST_FIRST ->
                                  entityRepository.findNextPageBySearchPattern(
                                      pattern,
                                      boundary.createdAt(),
                                      boundary.id(),
                                      criteria.pageRequest());
                              case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
                            })
                    .orElseGet(
                        () ->
                            switch (criteria.paginationCriteria().order()) {
                              case OLDEST_FIRST ->
                                  entityRepository.findNextPageForOldestFirst(
                                      boundary.createdAt(), boundary.id(), criteria.pageRequest());
                              case NEWEST_FIRST ->
                                  entityRepository.findNextPage(
                                      boundary.createdAt(), boundary.id(), criteria.pageRequest());
                              case NEAREST_FIRST -> throw new IllegalStateException("Unreachable");
                            }))
        .orElseGet(List::of);
  }

  private List<DonationEntity> fetchNextNearestPage(FetchCriteria criteria) {
    return criteria
        .proximityReference()
        .flatMap(
            reference ->
                criteria
                    .proximityBoundary()
                    .map(
                        boundary ->
                            criteria
                                .searchPattern()
                                .map(
                                    pattern ->
                                        entityRepository.findNextNearestFirstPageBySearchPattern(
                                            pattern,
                                            reference.latitude(),
                                            reference.longitude(),
                                            boundary.distanceMeters(),
                                            boundary.id(),
                                            criteria.pageRequest()))
                                .orElseGet(
                                    () ->
                                        entityRepository.findNextNearestFirstPage(
                                            reference.latitude(),
                                            reference.longitude(),
                                            boundary.distanceMeters(),
                                            boundary.id(),
                                            criteria.pageRequest()))))
        .orElseGet(List::of);
  }
}
