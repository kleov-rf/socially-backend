package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output;

import com.socially.auth.kernel.domain.AuthUser;

public record RefreshSessionResponse(
    String accessToken, String tokenType, long expiresIn, AuthUser user) {}
