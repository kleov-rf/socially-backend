package com.socially.user.me.application.output;

import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record UserMeQueryResult(User user, Optional<Donor> donor) {

  public static UserMeQueryResult create(User user) {
    return new UserMeQueryResult(user, Optional.empty());
  }

  public UserMeQueryResult withDonor(Optional<Donor> donor) {
    return new UserMeQueryResult(user, donor);
  }
}
