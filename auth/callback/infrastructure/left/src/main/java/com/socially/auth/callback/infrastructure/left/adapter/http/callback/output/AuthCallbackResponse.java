package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output;

import com.socially.auth.kernel.domain.UserResponseDto;

public record AuthCallbackResponse(
    String accessToken, String tokenType, long expiresIn, UserResponseDto user) {}
