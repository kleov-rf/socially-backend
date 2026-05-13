package com.socially.auth.refresh.application.output;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.user.kernel.domain.entity.User;

public record RefreshSessionCommandResult(
    AuthResult authResult, User user, CookieInstruction cookieInstruction) {}
