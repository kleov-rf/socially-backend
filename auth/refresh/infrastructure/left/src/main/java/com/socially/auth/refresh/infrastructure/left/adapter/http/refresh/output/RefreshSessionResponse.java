package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output;

import com.socially.auth.kernel.domain.UserResponseDto;

public record RefreshSessionResponse(
    String accessToken, String tokenType, long expiresIn, UserResponseDto user) {}
