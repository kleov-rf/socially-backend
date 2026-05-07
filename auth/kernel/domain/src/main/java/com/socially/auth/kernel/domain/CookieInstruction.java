package com.socially.auth.kernel.domain;

public record CookieInstruction(String name, String value, long maxAgeSeconds) {}
