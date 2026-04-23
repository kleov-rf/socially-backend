package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.List;

public record PageSlice(List<DonationEntity> entities, boolean overflowItemsExist) {}
