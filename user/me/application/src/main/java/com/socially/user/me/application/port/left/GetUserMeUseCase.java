package com.socially.user.me.application.port.left;

import com.socially.user.me.application.output.UserMeQueryResult;
import java.security.Principal;

public interface GetUserMeUseCase {
  UserMeQueryResult execute(Principal principal);
}
