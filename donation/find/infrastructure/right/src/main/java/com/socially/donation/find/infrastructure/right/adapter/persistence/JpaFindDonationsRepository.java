package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.List;
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
    List<DonationEntity> entities = fetchEntities(paginationCriteria.cursor(), pageSize + 1);
    boolean nextPageExists = entities.size() > pageSize;

    List<DonationEntity> pageEntities = getPageDonations(nextPageExists, entities, pageSize);

    String nextCursor = getNextCursor(nextPageExists, pageEntities);
    String previousCursor = getPreviousCursor(paginationCriteria.cursor(), pageEntities);
    boolean previousPageExists = Objects.nonNull(previousCursor);
    List<Donation> donations = pageEntities.stream().map(entityMapper::toDomain).toList();
    Metadata metadata =
        Metadata.create(nextCursor, previousCursor, nextPageExists, previousPageExists, pageSize);

    return Page.create(donations, metadata);
  }

  private List<DonationEntity> fetchEntities(String cursor, int fetchSize) {
    PageRequest pageRequest = PageRequest.of(0, fetchSize);

    if (Objects.isNull(cursor)) {
      return entityRepository.findByOrderByCreatedAtDescIdDesc(pageRequest);
    }

    KeysetCursorCodec.CursorBoundary boundary = cursorCodec.decode(cursor);
    return entityRepository.findNextPage(boundary.createdAt(), boundary.id(), pageRequest);
  }

  private static List<DonationEntity> getPageDonations(
      boolean nextPageExists, List<DonationEntity> fetchedEntities, int pageSize) {
    if (!nextPageExists) {
      return fetchedEntities;
    }
    return fetchedEntities.subList(0, pageSize);
  }

  private String getNextCursor(boolean nextPageExists, List<DonationEntity> donations) {
    if (!nextPageExists) {
      return null;
    }

    DonationEntity lastEntity = donations.getLast();
    return cursorCodec.encode(lastEntity.getCreatedAt(), lastEntity.getId());
  }

  private String getPreviousCursor(String cursor, List<DonationEntity> donations) {
    if (Objects.isNull(cursor) || donations.isEmpty()) {
      return null;
    }

    DonationEntity firstEntity = donations.getFirst();
    return cursorCodec.encode(firstEntity.getCreatedAt(), firstEntity.getId());
  }
}
