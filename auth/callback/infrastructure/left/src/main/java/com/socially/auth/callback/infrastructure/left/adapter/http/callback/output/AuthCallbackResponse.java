package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output;

public record AuthCallbackResponse(String accessToken, String tokenType, long expiresIn) {}
