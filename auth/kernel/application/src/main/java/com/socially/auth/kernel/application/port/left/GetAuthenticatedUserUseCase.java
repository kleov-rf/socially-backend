package com.socially.auth.kernel.application.port.left;

import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;

public interface GetAuthenticatedUserUseCase {
  User execute(Principal principal);
}
