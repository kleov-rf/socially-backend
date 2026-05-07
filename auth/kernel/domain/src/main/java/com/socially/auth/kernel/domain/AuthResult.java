package com.socially.auth.kernel.domain;

import org.springframework.lang.Nullable;

public record AuthResult(
    String accessToken, String tokenType, @Nullable Long expiresIn, AuthUser user) {}
