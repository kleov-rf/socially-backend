package com.socially.auth.kernel.domain.properties;

public record OAuthProperties(
    String region,
    String userPoolId,
    String hostedDomain,
    String baseUrl,
    String identityProvider,
    Boolean useMinistack,
    CredentialsProperties credentials) {}
