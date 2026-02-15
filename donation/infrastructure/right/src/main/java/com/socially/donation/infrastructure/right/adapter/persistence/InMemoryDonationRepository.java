package com.socially.donation.infrastructure.right.adapter.persistence;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.right.DonationRepository;
import com.socially.donation.domain.valueobject.Id;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryDonationRepository implements DonationRepository {

  private final Map<Id, Donation> storage = new ConcurrentHashMap<>();

  @Override
  public void save(Donation donation) {
    storage.put(donation.id(), donation);
  }

  @Override
  public Optional<Donation> findById(Id id) {
    return Optional.ofNullable(storage.get(id));
  }
}
