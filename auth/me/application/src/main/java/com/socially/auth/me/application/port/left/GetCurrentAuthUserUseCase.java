package com.socially.auth.me.application.port.left;

import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;

public interface GetCurrentAuthUserUseCase {
  User execute(Principal principal);
}
