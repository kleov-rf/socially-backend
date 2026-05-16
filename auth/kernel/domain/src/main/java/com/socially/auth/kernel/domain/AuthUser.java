package com.socially.auth.kernel.domain;

public record AuthUser(
    String issuer, String subject, String email, String givenName, String familyName) {}
