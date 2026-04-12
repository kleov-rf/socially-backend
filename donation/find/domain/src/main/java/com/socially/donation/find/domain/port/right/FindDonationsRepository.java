package com.socially.donation.find.domain.port.right;

import com.socially.donation.kernel.domain.entity.Donation;
import java.util.List;

public interface FindDonationsRepository {
  List<Donation> find();
}
