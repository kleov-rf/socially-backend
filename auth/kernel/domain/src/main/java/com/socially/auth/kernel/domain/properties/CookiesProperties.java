package com.socially.auth.kernel.domain.properties;

public record CookiesProperties(Boolean secure, CookieNamesProperties names) {
  public record CookieNamesProperties(String refresh, String state, String pkce) {}
}
