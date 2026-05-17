package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output;

public record RefreshSessionResponse(String accessToken, String tokenType, long expiresIn) {}
