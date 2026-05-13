package com.socially.auth.refresh.application.output;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.user.kernel.domain.entity.User;
import org.springframework.lang.Nullable;

public record RefreshSessionCommandResult(
    AuthResult authResult, User user, @Nullable CookieInstruction cookieInstruction) {
  public RefreshSessionCommandResult(
      AuthResult authResult, @Nullable CookieInstruction cookieInstruction) {
    this(authResult, null, cookieInstruction);
  }
}
