package com.socially.user.me.application.output;

import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;

public record UserMeQueryResult(User user, Optional<Donor> donor) {}
