package com.socially.auth.refresh.application.output;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.CookieInstruction;
import org.springframework.lang.Nullable;

public record RefreshSessionCommandResult(
    AuthResult authResult, @Nullable CookieInstruction cookieInstruction) {}
