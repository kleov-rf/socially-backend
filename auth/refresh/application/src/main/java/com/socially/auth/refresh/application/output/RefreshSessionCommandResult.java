package com.socially.auth.refresh.application.output;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RefreshSessionCommandResult(
    AuthResult authResult, User user, Optional<CookieInstruction> cookieInstruction) {

  public static RefreshSessionCommandResult create(AuthResult authResult, User user) {
    return new RefreshSessionCommandResult(authResult, user, Optional.empty());
  }

  public RefreshSessionCommandResult withCookieInstruction(
      Optional<CookieInstruction> cookieInstruction) {
    return new RefreshSessionCommandResult(authResult, user, cookieInstruction);
  }
}
