package com.socially.auth.me.application.port.left;

import com.socially.auth.me.application.output.AuthMeQueryResult;
import java.security.Principal;

public interface GetAuthMeUseCase {
  AuthMeQueryResult execute(Principal principal);
}
