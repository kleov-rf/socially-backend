package com.socially.auth.kernel.infrastructure.left;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationTest {

  @Mock private HttpSecurity http;
  @Mock private DefaultSecurityFilterChain securityFilterChain;

  private SecurityConfiguration sut;

  @BeforeEach
  void setUp() throws Exception {
    sut = new SecurityConfiguration();
    stubHttpChainReturns();
  }

  private void stubHttpChainReturns() throws Exception {
    when(http.csrf(any())).thenReturn(http);
    when(http.formLogin(any())).thenReturn(http);
    when(http.httpBasic(any())).thenReturn(http);
    when(http.logout(any())).thenReturn(http);
    when(http.authorizeHttpRequests(any())).thenReturn(http);
    lenient().when(http.oauth2ResourceServer(any())).thenReturn(http);
    when(http.build()).thenReturn(securityFilterChain);
  }

  @Test
  void securityFilterChain_should_disable_csrf() throws Exception {
    @SuppressWarnings("unchecked")
    ArgumentCaptor<Customizer<CsrfConfigurer<HttpSecurity>>> captor =
        ArgumentCaptor.forClass(Customizer.class);

    sut.securityFilterChain(http, "https://issuer.example.com");

    verify(http).csrf(captor.capture());
    CsrfConfigurer<HttpSecurity> csrfConfigurer = mock(CsrfConfigurer.class);
    when(csrfConfigurer.disable()).thenReturn(http);
    captor.getValue().customize(csrfConfigurer);
    verify(csrfConfigurer).disable();
  }

  @Test
  void securityFilterChain_should_disable_form_login() throws Exception {
    @SuppressWarnings("unchecked")
    ArgumentCaptor<Customizer<FormLoginConfigurer<HttpSecurity>>> captor =
        ArgumentCaptor.forClass(Customizer.class);

    sut.securityFilterChain(http, "https://issuer.example.com");

    verify(http).formLogin(captor.capture());
    FormLoginConfigurer<HttpSecurity> configurer = mock(FormLoginConfigurer.class);
    when(configurer.disable()).thenReturn(http);
    captor.getValue().customize(configurer);
    verify(configurer).disable();
  }

  @Test
  void securityFilterChain_should_disable_http_basic() throws Exception {
    @SuppressWarnings("unchecked")
    ArgumentCaptor<Customizer<HttpBasicConfigurer<HttpSecurity>>> captor =
        ArgumentCaptor.forClass(Customizer.class);

    sut.securityFilterChain(http, "https://issuer.example.com");

    verify(http).httpBasic(captor.capture());
    HttpBasicConfigurer<HttpSecurity> configurer = mock(HttpBasicConfigurer.class);
    when(configurer.disable()).thenReturn(http);
    captor.getValue().customize(configurer);
    verify(configurer).disable();
  }

  @Test
  void securityFilterChain_should_disable_logout() throws Exception {
    @SuppressWarnings("unchecked")
    ArgumentCaptor<Customizer<LogoutConfigurer<HttpSecurity>>> captor =
        ArgumentCaptor.forClass(Customizer.class);

    sut.securityFilterChain(http, "https://issuer.example.com");

    verify(http).logout(captor.capture());
    LogoutConfigurer<HttpSecurity> configurer = mock(LogoutConfigurer.class);
    when(configurer.disable()).thenReturn(http);
    captor.getValue().customize(configurer);
    verify(configurer).disable();
  }

  @Test
  void securityFilterChain_should_return_security_with_oauth2_resource_server() throws Exception {
    SecurityFilterChain chain = sut.securityFilterChain(http, "https://issuer.example.com");

    verify(http).oauth2ResourceServer(any());
    assertSame(securityFilterChain, chain);
  }

  @Test
  void
      securityFilterChain_should_not_return_security_with_oauth2_resource_server_when_issuer_uri_is_not_present()
          throws Exception {
    sut.securityFilterChain(http, "");

    verify(http, never()).oauth2ResourceServer(any());
  }

  @Test
  void securityFilterChain_should_configure_authorize_http_requests() throws Exception {
    sut.securityFilterChain(http, "https://issuer.example.com");

    verify(http).authorizeHttpRequests(any());
  }
}
