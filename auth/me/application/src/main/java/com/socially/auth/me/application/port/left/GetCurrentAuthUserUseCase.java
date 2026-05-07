package com.socially.auth.me.application.port.left;

import com.socially.auth.kernel.domain.AuthUser;
import java.security.Principal;

public interface GetCurrentAuthUserUseCase {
  AuthUser execute(Principal principal);
}
