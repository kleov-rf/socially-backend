package com.socially.auth.callback.application.output;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.user.kernel.domain.entity.User;
import java.util.List;

public record CompleteOAuthCallbackOutcome(
    AuthResult authResult, User user, List<CookieInstruction> cookieInstructions) {
  public CompleteOAuthCallbackOutcome(
      AuthResult authResult, List<CookieInstruction> cookieInstructions) {
    this(authResult, null, cookieInstructions);
  }
}
