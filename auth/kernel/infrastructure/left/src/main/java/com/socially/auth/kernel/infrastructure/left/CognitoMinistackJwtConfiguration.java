package com.socially.auth.kernel.infrastructure.left;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@ConditionalOnProperty(prefix = "auth.oauth", name = "use-ministack", havingValue = "true")
public class CognitoMinistackJwtConfiguration {

  public static String normalizeResourceIssuerUri(String resourceIssuerUri) {
    return resourceIssuerUri.endsWith("/")
        ? resourceIssuerUri.substring(0, resourceIssuerUri.length() - 1)
        : resourceIssuerUri;
  }

  public static String ministackJwkSetUri(String resourceIssuerUri) {
    return normalizeResourceIssuerUri(resourceIssuerUri) + "/.well-known/jwks.json";
  }

  public static String cognitoIdpIssuerClaim(String cognitoRegion, String userPoolId) {
    return "https://cognito-idp.%s.amazonaws.com/%s"
        .formatted(cognitoRegion.strip(), userPoolId.strip());
  }

  @Bean
  @Primary
  public JwtDecoder cognitoMinistackJwtDecoder(
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String resourceIssuerUri,
      @Value("${auth.oauth.region}") String cognitoRegion,
      @Value("${auth.oauth.user-pool-id}") String userPoolId) {
    String jwkSetUri = ministackJwkSetUri(resourceIssuerUri);
    String jwtIssuerClaim = cognitoIdpIssuerClaim(cognitoRegion, userPoolId);

    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtIssuerClaim));
    return decoder;
  }
}
