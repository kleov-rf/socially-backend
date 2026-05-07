package com.socially.auth.login.application.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class OAuthPkceService {
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

  public String generateCodeChallenge(String verifier) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] digest = messageDigest.digest(verifier.getBytes(StandardCharsets.UTF_8));
      return BASE64_URL_ENCODER.encodeToString(digest);
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Unable to generate PKCE code challenge", exception);
    }
  }

  public String generateRandomToken() {
    byte[] value = new byte[32];
    SECURE_RANDOM.nextBytes(value);
    return BASE64_URL_ENCODER.encodeToString(value);
  }
}
