package com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthSetCookieHeaderMapperTest {

  @Mock private AuthProperties authProperties;

  @InjectMocks private AuthSetCookieHeaderMapper sut;

  @BeforeEach
  void stubSecureDefault() {
    when(authProperties.cookiesSecure()).thenReturn(false);
  }

  @Test
  void toSetCookieHeader_should_return_cookie_with_received_name() {
    CookieInstruction instruction = new CookieInstruction("my_cookie", "v", 60);

    String header = sut.toSetCookieHeader(instruction);

    assertTrue(header.startsWith("my_cookie="));
  }

  @Test
  void toSetCookieHeader_should_return_cookie_with_received_value() {
    CookieInstruction instruction = new CookieInstruction("n", "secret-value", 60);

    String header = sut.toSetCookieHeader(instruction);

    assertTrue(header.contains("secret-value"));
  }

  @Test
  void toSetCookieHeader_should_return_cookie_with_http_only() {
    CookieInstruction instruction = new CookieInstruction("n", "v", 60);

    String header = sut.toSetCookieHeader(instruction);

    assertTrue(header.contains("HttpOnly"));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void toSetCookieHeader_should_return_cookie_with_cookies_secure(boolean secure) {
    when(authProperties.cookiesSecure()).thenReturn(secure);
    CookieInstruction instruction = new CookieInstruction("n", "v", 60);

    String header = sut.toSetCookieHeader(instruction);

    assertEquals(secure, header.contains("Secure"));
  }

  @Test
  void toSetCookieHeader_should_return_cookie_with_same_site() {
    CookieInstruction instruction = new CookieInstruction("n", "v", 60);

    String header = sut.toSetCookieHeader(instruction);

    assertTrue(header.contains("SameSite=Lax"));
  }

  @Test
  void toSetCookieHeader_should_return_cookie_with_path() {
    CookieInstruction instruction = new CookieInstruction("n", "v", 60);

    String header = sut.toSetCookieHeader(instruction);

    assertTrue(header.contains("Path=/"));
  }

  @Test
  void toSetCookieHeader_should_return_cookie_with_received_max_age() {
    CookieInstruction instruction = new CookieInstruction("n", "v", 7200);

    String header = sut.toSetCookieHeader(instruction);

    assertTrue(header.contains("Max-Age=7200"));
  }
}
