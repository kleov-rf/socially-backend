package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output;

import com.socially.auth.kernel.domain.AuthUser;

public record AuthCallbackResponse(
    String accessToken, String tokenType, long expiresIn, AuthUser user) {}
