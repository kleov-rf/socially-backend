package com.socially.user.updateprofile.application.input;

public record UpdateUserProfileCommand(
    String userId,
    String issuer,
    String subject,
    String email,
    String givenName,
    String familyName) {}
