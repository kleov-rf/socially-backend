package com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestCookiesMapperTest {

  @Mock private HttpServletRequest request;

  @InjectMocks private RequestCookiesMapper sut;

  @Test
  void toCookieMap_should_return_empty_map_when_received_request_cookies_are_not_present_null() {
    when(request.getCookies()).thenReturn(null);

    Map<String, String> map = sut.toCookieMap(request);

    assertTrue(map.isEmpty());
  }

  @Test
  void
      toCookieMap_should_return_empty_map_when_received_request_cookies_are_not_present_no_length() {
    when(request.getCookies()).thenReturn(new Cookie[0]);

    Map<String, String> map = sut.toCookieMap(request);

    assertTrue(map.isEmpty());
  }

  @Test
  void toCookieMap_should_return_map_with_cookie_name() {
    when(request.getCookies()).thenReturn(new Cookie[] {new Cookie("session_id", "abc")});

    Map<String, String> map = sut.toCookieMap(request);

    assertTrue(map.containsKey("session_id"));
  }

  @Test
  void toCookieMap_should_return_map_with_cookie_value() {
    when(request.getCookies()).thenReturn(new Cookie[] {new Cookie("session_id", "abc")});

    Map<String, String> map = sut.toCookieMap(request);

    assertEquals("abc", map.get("session_id"));
  }

  @Test
  void toCookieMap_should_return_map_with_cookies_when_received_multiple_cookies() {
    when(request.getCookies())
        .thenReturn(
            new Cookie[] {
              new Cookie("a", "1"), new Cookie("b", "2"),
            });

    Map<String, String> map = sut.toCookieMap(request);

    assertEquals(Map.of("a", "1", "b", "2"), map);
  }
}
