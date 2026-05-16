package com.socially.user.create.application.input;

public record CreateUserCommand(String userId, String email, String givenName, String familyName) {}
