package com.socially.user.federatedidentity.link.application.input;

public record LinkFederatedIdentityCommand(
    String userId, String issuer, String subject, String email) {}
