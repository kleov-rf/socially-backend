package com.socially.user.findbyfederatedidentity.application.port.left;

import com.socially.user.findbyfederatedidentity.application.input.FindUserByFederatedIdentityQuery;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;

public interface FindUserByFederatedIdentityUseCase {
  Optional<User> execute(FindUserByFederatedIdentityQuery query);
}
