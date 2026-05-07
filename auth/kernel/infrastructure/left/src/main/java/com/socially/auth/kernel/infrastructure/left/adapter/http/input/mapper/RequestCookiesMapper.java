package com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RequestCookiesMapper {

  public Map<String, String> toCookieMap(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) {
      return Map.of();
    }

    Map<String, String> values = new HashMap<>();
    Arrays.stream(cookies).forEach(cookie -> values.put(cookie.getName(), cookie.getValue()));
    return values;
  }
}
