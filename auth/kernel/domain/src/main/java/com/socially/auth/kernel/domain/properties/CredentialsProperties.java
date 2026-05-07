package com.socially.auth.kernel.domain.properties;

public record CredentialsProperties(Spa spa, Backend backend) {
  public record Spa(String clientId) {}

  public record Backend(String clientId, String clientSecretJson) {}
}
